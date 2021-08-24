package ReactiveRestServiceFunc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.bind.annotation.RequestHeader;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration(proxyBeanMethods = false)
public class Router {

    private Object serverRequest;
    private Object headers;

    @Bean
    public RouterFunction<ServerResponse> route(Handler handler) {

        return RouterFunctions.route()
            .GET("/hello", accept(MediaType.APPLICATION_JSON), handler::hello)
            .GET("/goodbye", accept(MediaType.APPLICATION_JSON), (request1)-> {
                Mono<ServerResponse> goodbye = handler.goodbye((ServerRequest) serverRequest);
                return goodbye;
            })
            .GET("/goodbye2Params", accept(MediaType.APPLICATION_JSON), (request1)-> {
                //you can't pass 2 params into this lambda for the same reason - the RouterFunction.builder.GET method only expects one :(
                Mono<ServerResponse> goodbye = handler.goodbye2Params((ServerRequest) serverRequest, (HttpHeaders) headers);
                // The headers are passed in as null here because RouterFunction is a @FunctionalInterface and it's route method
                // can only take a single argument - ServerRequest :  Mono<HandlerFunction<T>> route(ServerRequest var1);
                return goodbye;
            })
            .build();
    }
}