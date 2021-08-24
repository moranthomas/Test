package ReactiveRestServiceAnnot;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RestController
public class Controller {

    @GetMapping("/hello")
    public Mono<ServerResponse> hello(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(new ReactiveRestServiceFunc.Greeting("Hello, Spring!")));
    }

    @GetMapping("/updateUserById")
    public Mono<ServerResponse> updateUserById(@PathVariable Integer userId, @RequestBody Greeting greeting){
        /*return userService.updateUser(userId,user)
            .map(updatedUser -> ResponseEntity.ok(updatedUser))
            .defaultIfEmpty(ResponseEntity.badRequest().build());*/

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(new ReactiveRestServiceFunc.Greeting("User, Updated!")));
    }
}