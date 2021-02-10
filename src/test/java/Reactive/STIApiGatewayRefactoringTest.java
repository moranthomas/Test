package Reactive;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Slf4j
public class STIApiGatewayRefactoringTest {

    @Test
    public Mono<ServerResponse> StartupLayer_publish(ServerRequest request) {

        ServerResponse response = null;
        final ObjectMapper objectMapper = new ObjectMapper();

        return request.body(BodyExtractors.toMono(String.class))
                .flatMap(m -> {

                    PubSubEvent event = Try.of(() -> objectMapper.readValue(m, PubSubEvent.class))
                            .onFailure(t -> log.warn("Unable to map JSON to to PubSubEvent.", t))
                            .getOrNull();
                    return
                        Try.of(() -> {
                            return Mono.just((ServerResponse) ServiceLayer_publishEventToQueue(event, m).getLeft());
                            }
                        )
                            .onFailure(t -> Mono.error(new RuntimeException("Unable to publish", t)))
                            .get();
                })
                .onErrorResume(t -> Mono.error(new RuntimeException("Flux mapping error", t)));
    }

    public MutablePair ServiceLayer_publishEventToQueue (PubSubEvent event, String jsonMessage) {

        final RabbitQueueConfiguration rabbitQueueConfiguration = null;

        MutablePair<ServerResponse, Exception> ResponseExceptionPair = new MutablePair<>();

        Map<String, String> tokenMappings = Collections.unmodifiableMap(new HashMap<String, String>() {{
            put("TOKEN", rabbitQueueConfiguration.getTokenQueueName());
            put("TRAN", rabbitQueueConfiguration.getTransactionQueueName());
        }});

        Try.of(() -> {
                    String queueName = tokenMappings.get(event.getEventType());
                    if (queueName == null) {
                        ResponseExceptionPair.setRight(new Exception("Invalid message type"));
                        //ResponseExceptionPair.setLeft(ServerResponse.status(HttpStatus.NOT_FOUND).contentLength(MediaType.APPLICATION_JSON));
                    }
                    //publishToQueue(queueName, jsonMessage);

                    ResponseExceptionPair.setLeft((ServerResponse) ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.empty()));
                    return ResponseExceptionPair;
                }
        )
                .onFailure(t -> new RuntimeException("Unable to publish", t))
                .get();

        return ResponseExceptionPair;

    }


    @Test
    public void ServerResponseTest() {
        Try.of(() -> {
                Mono<ServerResponse> hello = ok().contentType(MediaType.TEXT_PLAIN) .body(BodyInserters.fromValue("Hello, Spring!"));
                //assertThat(hello.flatMap()).isEqualTo();
                assertThat(hello.getClass()).isNotEqualTo(Mono.class);
                    return true;
                }
        )
        .onFailure(t -> new RuntimeException("Unable to publish", t))
        .get();
    }

    /*private Try<Response> lambdaReturn() {

        return {
            Try.of(() -> {
                Mono<ServerResponse> hello = ok().contentType(MediaType.TEXT_PLAIN) .body(BodyInserters.fromValue("Hello, Spring!"));
                //assertThat(hello.flatMap()).isEqualTo();
                assertThat(hello.getClass()).isNotEqualTo(Mono.class);
                return true;
                    }
            )
            .onFailure(t -> { new RuntimeException("Unable to publish", t)})
            .get();
        }
        //return false;
    }*/

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
