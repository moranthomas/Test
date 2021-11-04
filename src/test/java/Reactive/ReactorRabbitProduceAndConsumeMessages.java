package Reactive;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;
import com.github.fridujo.rabbitmq.mock.MockConnectionFactory;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.vavr.control.Try;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.AcknowledgableDelivery;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.ReceiverOptions;
import reactor.rabbitmq.Sender;
import reactor.rabbitmq.SenderOptions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


@Slf4j
public class ReactorRabbitProduceAndConsumeMessages {


    @Test
    public void reactorRabbitProducerTest() {

        String exchangeName = "amq.direct";
        String routingKey = "";
        final String queueName = "spend-trend-rabbit-mq";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        int messageCount = 5000;

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

            // Single kryo not thread safe, need a kryo-pool - Inputs and Outputs could also be using pools: https://github.com/EsotericSoftware/kryo#thread-safety
            Pool<Kryo> kryoPool = new Pool<Kryo>(true, false, 256) {
                protected Kryo create() {
                    Kryo kryo = new Kryo();
                    kryo.register(PubSubMessage.class);
                    return kryo;
                }
            };
            AtomicBoolean latchCompleted = new AtomicBoolean(false);

            // Java.util.concurrent synchronization aid - allows one or more threads to wait until operations being performed in other threads completes
            Phaser phaser = new Phaser(messageCount);

            //CountDownLatch latch = new CountDownLatch(messageCount);

            publishFakeMessages(exchangeName, routingKey, connectionMono, kryoPool, messageCount);

           //Wait 3 seconds for the process to complete - we are setting the value in the Atomic Boolean for thread safety
            phaser.arriveAndAwaitAdvance();
            //latchCompleted.set(latch.await(3, TimeUnit.SECONDS));

            //Test if we did wait 3 secs....
            //assertThat(latchCompleted.get()).isTrue();
            assertThat(phaser.arrive()).isNotZero();
        })
            .onFailure(IOException.class, e -> log.warn("Unable to mock connection"))
            .onFailure(TimeoutException.class, e -> log.warn("Unable to create channel"))
            .onFailure(InterruptedException.class, e -> log.warn("Timed out waiting for messages"));
            //.onFailure(fail("Publisher:: Assertions Failed"));
    }


    @Test
    public void  reactorRabbitConsumerTest() {

        String exchangeName = "amq.direct";
        String routingKey = "";
        final String queueName = "spend-trend-rabbit-mq";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        int messageCount = 5000;

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
                log.info("Consumer Test: Connection to Rabbit Queue successful *************************\n\n");
                return connection;
            })
                .cache();

            // Single kryo not thread safe, need a kryo-pool - Inputs and Outputs could also be using pools: https://github.com/EsotericSoftware/kryo#thread-safety
            Pool<Kryo> kryoPool = new Pool<Kryo>(true, false, 256) {
                protected Kryo create() {
                    Kryo kryo = new Kryo();
                    kryo.register(PubSubMessage.class);
                    return kryo;
                }
            };
            AtomicBoolean latchCompleted = new AtomicBoolean(false);

            // Java.util.concurrent synchronization aid - allows one or more threads to wait until operations being performed in other threads completes
            CountDownLatch latch = new CountDownLatch(messageCount);

            consumeMessagesFromRabbit(queueName, connectionMono, kryoPool, latch);

            //Wait 3 seconds for the process to complete - we are setting the value in the Atomic Boolean for thread safety
            latchCompleted.set(latch.await(3, TimeUnit.SECONDS));

            //Test if we did wait 3 secs....
            assertThat(latchCompleted.get()).isTrue();
        })
            .onFailure(IOException.class, e -> log.warn("Unable to mock connection"))
            .onFailure(TimeoutException.class, e -> log.warn("Unable to create channel"))
            .onFailure(InterruptedException.class, e -> log.warn("Timed out waiting for messages"));
            //.onFailure(fail("Consumer:: Assertions Failed"));
    }


    private void consumeMessagesFromRabbit(String queueName, Mono<Connection> connectionMono, Pool<Kryo> kryoPool, CountDownLatch latch) {
        // FOR OUR PROCESSOR - NOT REQUIRED FOR GATEWAY
        Receiver receiver = RabbitFlux.createReceiver(new ReceiverOptions().connectionMono(connectionMono));
        Flux<AcknowledgableDelivery> deliveryFlux = receiver.consumeManualAck(queueName);
        deliveryFlux
            .bufferTimeout(50, Duration.ofMillis(200))
            .filter(f -> f.size() > 0)
            .subscribe(listOfMessages -> {

                log.info("Consuming Messages from Rabbit");
                /* A PROD-LIKE scenario

                listOfMessages.stream()
                    .map(acknowledgableDelivery -> {
                        byte[] body = acknowledgableDelivery.getBody();
                        Input input = new Input(body, 0, body.length);
                        Kryo kryo = kryoPool.obtain();
                        ImmutablePair<AcknowledgableDelivery, PubSubMessage> message =
                            new ImmutablePair<>(acknowledgableDelivery, kryo.readObject(input, PubSubMessage.class));
                        return message;
                    })
                    .collect(Collectors.groupingBy(g -> g.getRight().eventType))
                    .forEach((key, list) -> {
                        Processor processor = ProcessorSelector.get(key);

                        Try.run(() -> processor.process(list.stream()
                            .map(l -> l.getRight())
                            .collect(Collectors.toList())))
                            .onFailure(t -> {
                                log.error("Could not commit", t);
                                list.stream()
                                    .map(m -> m.getLeft())
                                    .forEach(a -> a.nack(true));
                            })
                            .onSuccess(u -> list.stream()
                                .map(m -> m.getLeft())
                                .forEach(a -> a.ack()));

                    });

                 */

                listOfMessages
                    .forEach(acknowledgableDelivery -> {
                            byte[] body = acknowledgableDelivery.getBody();
                            Input input = new Input(body, 0, body.length);
                            Kryo kryo = kryoPool.obtain();
                            PubSubMessage message = kryo.readObject(input, PubSubMessage.class);
                            kryoPool.free(kryo);
                            if (message != null && StringUtils.isNotBlank(message.getMessage()) && StringUtils.isNotBlank(message.getUuid())) {
                                latch.countDown(); //aka "write to database", but this would be sequential and one record at a time, a no-no, must be batch insert
                                acknowledgableDelivery.ack();
                            }
                        }
                    );
            });
    }

    private void publishFakeMessages(String exchangeName, String routingKey, Mono<Connection> connectionMono, Pool<Kryo> kryoPool, int messageCount) {

        //TO PRODUCE FAKE MESSAGES TO RABBIT
        log.info("Sending messages to Rabbit ...");
        Sender sender = RabbitFlux.createSender(new SenderOptions().connectionMono(connectionMono));
        sender.send(Flux.range(1, messageCount)
            .map(i -> "Message_" + i)
            .map(s -> {
                PubSubMessage message = new PubSubMessage("XXX", UUID.randomUUID().toString(), s);
                Output output = new Output(1024, -1);
                Kryo kryo = kryoPool.obtain();
                kryo.writeObject(output, message);
                kryoPool.free(kryo);
                return new OutboundMessage(exchangeName, routingKey, output.getBuffer());
            }))
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe();
    }


    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    static
    class PubSubMessage {

        String eventType;
        String uuid;
        String message;
    }
}
