package Reactive;

import io.vavr.control.Try;
import org.apache.commons.lang3.tuple.MutablePair;
import org.h2.tools.Server;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

public class RefactorTest {

    @Test
    public MutablePair test1 () {

        final RabbitQueueConfiguration rabbitQueueConfiguration = null;
        MutablePair<ServerResponse, Exception> ResponseExceptionPair = new MutablePair<>();
        PubSubEvent event = new PubSubEvent();
        event.setEventType("TOKEN");

        Map<String, String> tokenMappings = Collections.unmodifiableMap(new HashMap<String, String>() {{
            put("TOKEN", rabbitQueueConfiguration.getTokenQueueName());
            put("TRAN", rabbitQueueConfiguration.getTransactionQueueName());
        }});
        Try.of(() -> {
                    String queueName = tokenMappings.get(event.getEventType());
                    if (queueName == null)
                        ResponseExceptionPair.setRight(new Exception("Invalid message type"));

                    //publishToQueue(queueName, jsonMessage);


                    // ResponseExceptionPair.setLeft((ServerResponse) ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.empty()));
                    return ResponseExceptionPair;
                }
        )
                .onFailure(t -> new RuntimeException("Unable to publish", t))
                .get();

        return ResponseExceptionPair;

    }

    @Test
    public void ServerResponseTest() {
         Mono<ServerResponse> hello = ServerResponse.ok().contentType(MediaType.TEXT_PLAIN) .body(BodyInserters.fromValue("Hello, Spring!"));
        //assertThat(hello.flatMap()).isEqualTo();
        assertThat(hello.getClass()).isNotEqualTo(Mono.class);
    }

    public class PubSubEvent {
        private String eventType;

        private void setEventType(String type) {
            this.eventType = type;
        }

        public String getEventType() {
            return this.eventType;
        }
    }

    interface RabbitQueueConfiguration {
        String getTokenQueueName();
        String getTransactionQueueName();
        String getHostname();
        int getPort();
        String getUser();
        String getPassword();
    }


}
