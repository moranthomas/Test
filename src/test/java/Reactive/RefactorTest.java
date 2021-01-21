package Reactive;

import io.vavr.control.Try;
import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RefactorTest {

    @Test
    public MutablePair test1 () {

        MutablePair<ServerResponse, Exception> ResponseExceptionPair = new MutablePair<>();

        Map<String, String> tokenMappings = Collections.unmodifiableMap(new HashMap<String, String>() {{
            put("TOKEN", rabbitQueueConfiguration.getTokenQueueName());
            put("TRAN", rabbitQueueConfiguration.getTransactionQueueName());
        }});
        Try.of(() -> {
                    String queueName = tokenMappings.get(event.getEventType());
                    if (queueName == null)
                        ResponseExceptionPair.setRight(new Exception("Invalid message type"));
                    publishToQueue(queueName, jsonMessage);
                    ResponseExceptionPair.setLeft((ServerResponse) ok().contentType(MediaType.APPLICATION_JSON)
                            .body(BodyInserters.empty()));
                    return ResponseExceptionPair;
                }
        )
                .onFailure(t -> new RuntimeException("Unable to publish", t))
                .get();

        return ResponseExceptionPair;

    }


}
