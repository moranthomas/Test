import com.github.fridujo.rabbitmq.mock.MockChannel;
import com.github.fridujo.rabbitmq.mock.MockConnection;
import com.github.fridujo.rabbitmq.mock.MockConnectionFactory;
import com.oath.cyclops.async.QueueFactories;
import com.oath.cyclops.async.adapters.Queue;
import com.rabbitmq.client.*;
import cyclops.control.Future;
import cyclops.futurestream.LazyReact;
import cyclops.reactive.ReactiveSeq;
import cyclops.stream.StreamSource;
import io.vavr.control.Try;
import lombok.Value;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class ReactiveRabbitTests {

    @Test
    public void rabbitProcessorMustBeAvailable() {
        RabbitProcessor rabbitProcessor = new RabbitProcessor();
        rabbitProcessor.subscribe();
    }

    @Test
    public void messageChunkingExample() {
        String exchangeName = "test-exchange";
        String routingKey = "test.key";
        String whoAmI =
                Try.of(() -> InetAddress.getLocalHost().getHostName()).getOrElse(UUID.randomUUID().toString());

        ExecutorService executorService = Executors.newCachedThreadPool();
        Scheduler scheduler = Schedulers.fromExecutor(executorService);

        CircuitBreaker globalBreaker = CircuitBreaker.ofDefaults("global");

        Try.run(() -> {

            //Fake RabbitMQ implementation courtesy of frudujo
            //https://github.com/fridujo/rabbitmq-mock
            Connection conn = new MockConnectionFactory().newConnection();
            assertThat(conn).isInstanceOf(MockConnection.class);

            Channel channel = conn.createChannel();
            assertThat(channel).isInstanceOf(MockChannel.class);

            channel.exchangeDeclare(exchangeName, "direct", true);
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchangeName, routingKey);

            //How big the test shall be
            AtomicInteger countOfGenerated = new AtomicInteger();
            AtomicInteger countOfAcknowledged = new AtomicInteger();

            //This will link connect RabbitMQ and DB processing
            Queue<PubSubMessage> pubSubMessageQueue = QueueFactories.<PubSubMessage>unboundedQueue().build();

            //Flux Consumer of messages using buffer of 50 or a forced flush five times a second
            //This part should be transactional database INSERT with an Array in one clean swoop
            Future<Void> str = StreamSource.futureStream(pubSubMessageQueue, new LazyReact(Executors.newCachedThreadPool()))
                    .parallel()
                    .groupedBySizeAndTime(50, 200, TimeUnit.MILLISECONDS)
                    .runFuture(Executors.newCachedThreadPool(), vectors -> vectors.forEach(
                            bufferedMessages -> {
                                log.debug("Received {} messages in Flux", bufferedMessages.size());

                                //This part should be transactional database INSERT with an Array in one clean swoop
                                bufferedMessages.forEach(pubSubMessage -> {
                                            long deliveryTag = pubSubMessage.getEnvelope().getDeliveryTag();
                                            Try.run(() -> channel.basicAck(deliveryTag, false))
                                                    .onFailure(e -> log.error("Unable to acknowledge, e"))
                                                    .onSuccess(unused -> {
                                                        countOfAcknowledged.addAndGet(1);
                                                        log.debug("Message {} acknowledged {}", deliveryTag,
                                                                new String(pubSubMessage.getBody()));
                                                    });
                                        }
                                );

                            }
                    ));

            //Create RabbitMQ consumer, producing messages to process through the queue
            DefaultConsumer rabbitConsumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body) {
                    String uuid = new String(body);
                    PubSubMessage pubSubMessage = new PubSubMessage(consumerTag, envelope, properties, body);
                    boolean result = pubSubMessageQueue.offer(pubSubMessage);
                    log.debug("{} message to queue {}", (result ? "Sent" : "Failed"), uuid);
                }
            };

            //Register RabbitMQ consumer
            Try.of(() -> channel.basicConsume(queueName, false, whoAmI, rabbitConsumer))
                    .onFailure(e -> log.error("Unable to register", e))
                    .onSuccess(s -> log.info("basicConsume returned {}", s));

            //This will generate as many messages as possible
            //In reality, it would be a bunch of servers sending messages to RabbitMQ
            //The fake implementation supports byte arrays only
            ReactiveSeq.generate(UUID::randomUUID)
                    .takeWhile(u -> globalBreaker.getState() == CircuitBreaker.State.CLOSED)
                    .runFuture(executorService, uuids -> uuids.forEach(uuid ->
                            Try.run(() -> channel.basicPublish(exchangeName, routingKey,
                                    null, uuid.toString().getBytes()))
                                    .onSuccess(un -> countOfGenerated.addAndGet(1))
                                    .onFailure(e -> log.warn("Unable to send RabbitMQ message", e))));

            //Wait for results up to 5 seconds, it's a test
            TimeUnit.SECONDS.sleep(10);

            //Circuit breaker
            globalBreaker.transitionToOpenState();
            //Not too happy about this possible bug
            //https://stackoverflow.com/questions/42363084/using-cyclops-react-for-batching-on-a-async-queue-stream
            long waitUntil = System.currentTimeMillis() + 1000;
            while (countOfAcknowledged.get() != countOfGenerated.get() && System.currentTimeMillis() <= waitUntil) {
                TimeUnit.MILLISECONDS.sleep(10);
            }
            pubSubMessageQueue.close();

            waitUntil = System.currentTimeMillis() + 5000;
            while (countOfAcknowledged.get() != countOfGenerated.get() && System.currentTimeMillis() <= waitUntil) {
                TimeUnit.MILLISECONDS.sleep(10);
            }

            log.info("Processed {}/{} messages", countOfAcknowledged.get(), countOfGenerated.get());
            assertEquals(countOfGenerated.get(), countOfAcknowledged.get());
        })
                .onFailure(IOException.class, e -> log.warn("Unable to mock connection"))
                .onFailure(TimeoutException.class, e -> log.warn("Unable to create channel"))
                .onFailure(InterruptedException.class, e -> log.warn("Timed out waiting for messages"))
                .onFailure(Assertions::fail);
    }

    @Value
    class PubSubMessage {
        String consumerTag;
        Envelope envelope;
        AMQP.BasicProperties properties;
        byte[] body;
    }

}
