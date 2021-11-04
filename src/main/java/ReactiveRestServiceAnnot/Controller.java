package ReactiveRestServiceAnnot;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RestController
public class Controller {

    @GetMapping("/hello")
    public Mono<ServerResponse> hello(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(new ReactiveRestServiceAnnot.Greeting("Hello, Spring!")));
    }

    @PostMapping("/updateUserById/{userId}")
    public Mono<ServerResponse> updateUserById(@PathVariable Integer userId, @RequestBody Greeting greeting){
        log.info(String.valueOf(userId));
        log.info(String.valueOf(greeting));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(new ReactiveRestServiceAnnot.Greeting("User, Updated!")));
    }

    @GetMapping(value = "/all")
    @ResponseStatus(value = HttpStatus.OK)
    public void all(@RequestHeader Map<String, String> headers) {
        log.info("All headers: {}", headers);
    }

}