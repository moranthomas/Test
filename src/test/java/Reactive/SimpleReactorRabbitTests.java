package Reactive;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.vavr.control.Try;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.OutboundMessageResult;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.Sender;
import reactor.rabbitmq.SenderOptions;

@Slf4j
public class SimpleReactorRabbitTests {

    String exchangeName = "amq.direct";
    String routingKey = "";
    String queueName = "spend-trend-rabbit-mq";
    ConnectionFactory factory = new ConnectionFactory();
    int messageCount;

    private final Receiver receiver = RabbitFlux.createReceiver();
    private final Sender sender = RabbitFlux.createSender();

    SenderOptions senderOptions =  new SenderOptions()
        .connectionFactory(factory)
        .connectionSupplier(cf -> cf.newConnection(
            new Address[] {new Address("192.168.0.1"), new Address("192.168.0.2")},
            "reactive-sender"))
        .resourceManagementScheduler(Schedulers.boundedElastic());

    private void setUpRabbitConnection() {
        Try.run(() -> {
            Mono<Connection> connectionMono = Mono.fromCallable(() -> {

                /*** Use com.github.fridujo.rabbitmq.mock for Tests **/
                //Connection FakeRabbitConnection = new MockConnectionFactory().newConnection();
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();

                // Create an exchange - rabbitMQ will just ignore the request if there is already one declared.
                channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT, true);
                log.info("Declared Exchange: " + exchangeName);

                // Create a queue - The declaration will have no effect if the queue does already exist.
                channel.queueDeclare(queueName, true, false, false, null);
                log.info("Declared Queue: " + queueName);

                // Bindings are nothing but routing between Exchanges and Queues. Messages are never published directly to a Queue.
                channel.queueBind(queueName, exchangeName, routingKey);
                log.info("Producer Test: Connection to Rabbit Queue successful *************************\n\n");
                return connection;
            })
                .cache();
        })
            .onFailure(IOException.class, e -> log.warn("Unable to mock connection"))
            .onFailure(TimeoutException.class, e -> log.warn("Unable to create channel"))
            .onFailure(InterruptedException.class, e -> log.warn("Timed out waiting for messages"));
        //.onFailure(fail("Publisher:: Assertions Failed"));
    }

    @Test
    public void testSender() throws InterruptedException {

        setUpRabbitConnection();

        int count = 20;
        CountDownLatch latch = new CountDownLatch(count);
        SampleSender sender = new SampleSender();
        factory.setHost("localhost");
        messageCount = 5000;

        sender.send(queueName, count, latch);
        //Disposable disposable = receiver.consume(queueName, latch);

        latch.await(3, TimeUnit.SECONDS);
        this.sender.close();
    }

    @Test
    public void testReceiver() throws InterruptedException {

        setUpRabbitConnection();
        int count = 20;
        factory.setHost("localhost");
        messageCount = 5000;

        CountDownLatch latch = new CountDownLatch(count);
        SampleReceiver receiver = new SampleReceiver();

        Disposable disposable = receiver.consume(queueName, latch);

        latch.await(3, TimeUnit.SECONDS);
        disposable.dispose();
        this.receiver.close();
    }


    class SampleSender {

        public void send(String queue, int count, CountDownLatch latch) {
            Flux<OutboundMessageResult> confirmations = sender.sendWithPublishConfirms(Flux.range(1, count)
                .map(i -> new OutboundMessage("", queue, ("Message_" + i).getBytes())));

            sender.declareQueue(QueueSpecification.queue(queue).durable(true))
                .thenMany(confirmations)
                .doOnError(e -> log.error("Send failed", e))
                .subscribe(r -> {
                    if (r.isAck()) {
                        log.info("Message {} sent successfully", new String(r.getOutboundMessage().getBody()));
                        latch.countDown();
                    }
                });
        }
    }


    class SampleReceiver {

        public Disposable consume(String queue, CountDownLatch latch) {
            return receiver.consumeAutoAck(queue)
                .delaySubscription(sender.declareQueue(QueueSpecification.queue(queue).durable(true)))
                .subscribe(m -> {
                    log.info("Received message {}", new String(m.getBody()));
                    latch.countDown();
                });
        }
    }

}
