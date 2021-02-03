package Reactive;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.rabbitmq.client.*;
import com.rabbitmq.client.AMQP.BasicProperties;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreaker.State;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.vavr.control.Try;
import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RxJavaRabbitExecutorTests {

    private static final Logger log = LoggerFactory.getLogger(RxJavaRabbitExecutorTests.class);

    @Test
    public void messageChunkingExample() {
        String exchangeName = "amq.direct";
        String routingKey = "";
        String whoAmI =
                Try.of(() -> InetAddress.getLocalHost()
                        .getHostName())
                        .getOrElse(UUID.randomUUID()
                                .toString());

        ExecutorService executorService = Executors.newCachedThreadPool();
        Scheduler scheduler = Schedulers.from(executorService);

        CircuitBreaker globalBreaker = CircuitBreaker.ofDefaults("global");

        Try.run(() -> {

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection;
            Channel channel;
            final String queueName = "spend-trend-rabbit-mq";

            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(queueName, true, false, false, null);
            log.info("Consume Test: RxJava Service Connection to Rabbit Queue successful *************************\n\n");


            //How big the test shall be
            AtomicInteger countOfGenerated = new AtomicInteger();
            AtomicInteger countOfRemaining = new AtomicInteger();
            AtomicInteger countOfAcknowledged = new AtomicInteger();

            //Flux Consumer of messages using buffer of 50 or a forced flush five times a second
            //This part should be transactional database INSERT with an Array in one clean swoop
            Disposable rabbitObserver = Observable.<PubSubMessage>create(emitter -> {
                //Create RabbitMQ consumer, producing messages to process through the messageBus
                DefaultConsumer rabbitConsumer = new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag,
                                               Envelope envelope,
                                               BasicProperties properties,
                                               byte[] body) {
                        String uuid = new String(body);
                        countOfRemaining.addAndGet(1);
                        PubSubMessage pubSubMessage = new PubSubMessage(consumerTag, envelope, properties, body);
                        emitter.onNext(pubSubMessage);
                        log.debug("Published message to queue {}", uuid);
                    }
                };

                //Register RabbitMQ consumer
                Try.of(() -> channel.basicConsume(queueName, false, whoAmI, rabbitConsumer))
                        .onFailure(e -> log.error("Unable to register", e))
                        .onSuccess(s -> log.info("basicConsume returned {}", s));

                while (globalBreaker.getState() == State.CLOSED) {
                    TimeUnit.MILLISECONDS.sleep(1);
                }
                channel.basicCancel(whoAmI);

                //We have to let the received messages to be acknowledged
                while (countOfRemaining.get() >0 ) {
                    TimeUnit.MILLISECONDS.sleep(1);
                }
                emitter.onComplete();

            })
                    .subscribeOn(scheduler)
                    .observeOn(scheduler)
                    .buffer(200, TimeUnit.MILLISECONDS, 50)
                    .filter(f -> f.size() > 0)
                    .doOnComplete(() -> log.info("messageBus signaled complete"))
                    .subscribe(pubSubMessages -> {
                        log.debug("Received {} messages in Flux", pubSubMessages.size());
                        pubSubMessages.forEach(pubSubMessage -> {
                                    long deliveryTag = pubSubMessage.getEnvelope()
                                            .getDeliveryTag();
                                    Try.run(() -> channel.basicAck(deliveryTag, false))
                                            .onFailure(e -> log.error("Unable to acknowledge, e"))
                                            .onSuccess(unused -> {
                                                countOfAcknowledged.addAndGet(1);
                                                countOfRemaining.addAndGet(-1);
                                                log.debug("Message {} acknowledged {}", deliveryTag,
                                                        new String(pubSubMessage.getBody()));
                                            });
                                }
                        );
                    });

            //This will generate as many messages as possible
            //In reality, it would be a bunch of servers sending messages to RabbitMQ
            //The fake implementation supports byte arrays only
            Disposable rabbitProducer = Flowable.generate(emitter -> emitter.onNext(UUID.randomUUID()))
                    .observeOn(scheduler)
                    .takeWhile(u -> globalBreaker.getState() == CircuitBreaker.State.CLOSED)
                    .subscribe(uuid ->
                            Try.run(() -> channel.basicPublish(exchangeName, routingKey,
                                    null, uuid.toString()
                                            .getBytes()))
                                    .onSuccess(un -> countOfGenerated.addAndGet(1))
                                    .onFailure(e -> log.warn("Unable to send RabbitMQ message", e)));

            //Wait for results up to 10 seconds, it's a test
            TimeUnit.SECONDS.sleep(5);
            //Circuit breaker
            globalBreaker.transitionToOpenState();

            //We have to wait for it to complete
            while (!rabbitObserver.isDisposed()) {
                TimeUnit.MILLISECONDS.sleep(1);
            }

            log.info("Processed {}/{} messages. {} would remain in RabbitMQ.", countOfAcknowledged.get(), countOfGenerated.get(),
                    countOfGenerated.get() - countOfAcknowledged.get());
            assertEquals(0, countOfRemaining.get());
        })
                .onFailure(IOException.class, e -> log.warn("Unable to mock connection"))
                .onFailure(TimeoutException.class, e -> log.warn("Unable to create channel"))
                .onFailure(InterruptedException.class, e -> log.warn("Timed out waiting for messages"))
                .onFailure(Assertions::fail);
    }

    @Value
    static
    class PubSubMessage {

        String consumerTag;
        Envelope envelope;
        AMQP.BasicProperties properties;
        byte[] body;
    }

}