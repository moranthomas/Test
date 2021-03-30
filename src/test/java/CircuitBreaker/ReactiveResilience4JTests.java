package CircuitBreaker;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Mono;

@Slf4j
public class ReactiveResilience4JTests {


    /* BASIC example without reactor and webclient
        private final ReactiveCircuitBreakerFactory circuitBreakerFactory;
        ReactiveCircuitBreaker reactiveCircuitBreaker = circuitBreakerFactory.create("rabbit-producer-breaker");

        Mono<Sting> httpCall = this.client
            .get()
            .bodyToMono()
            .map(Greetin:message);

        // wraps all your business logic with the circuit breaker here, the second arg is the fallback method
        reactiveCircuitBreaker
            .run( httpCall,  throwable -> Mono.just("CALL THE FALLBACK METHOD From CACHE"))
            .subscribe( greeting  -> log.info ( "Mono" + greeting )) ;
    )*/

    // Functional Interface - Only one abstract method.
    interface HelloWorldService {

        //abstract String sayHelloWorld();

        String sayHelloWorld(String s);
    }

    private HelloWorldService helloWorldService;
    CircuitBreaker circuitBreaker;

    @Test
    public void basicCircuitBreakerShouldWorksAsExpected() throws Exception {

        // Given I have a helloWorldService.sayHelloWorld() method which doesn't take too long (no artificial wait)
        HelloWorldService helloWorldService = new HelloWorldService() {
            @Override
            public String sayHelloWorld(String s) {
                String result = null;
                try{
                    //Introduce an artificial wait - but keep it below the 1 second threshold
                    result = "Hello World!";
                    Thread.sleep(20);
                }
                catch (InterruptedException ie) {
                    log.error(String.valueOf(ie));
                }
                //return circuitBreaker.executeSupplier(() -> helloWorldService.sayHelloWorld("Hello World!"));
                return result;
            }

        };


        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(1)
            .waitDurationInOpenState(Duration.ofMillis(1000))
            .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        circuitBreaker = registry.circuitBreaker("rabbit-producer-circuit-breaker", config);

        String decoratedResult = circuitBreaker.executeSupplier(() -> helloWorldService.sayHelloWorld("Hello World!"));

       /* Supplier<String> decoratedSupplier = CircuitBreaker.decorateSupplier(circuitBreaker, helloWorldService::sayHelloWorld);
        Function<String, String> decoratedResult = CircuitBreaker.decorateFunction(circuitBreaker, helloWorldService::sayHelloWorld);*/

        log.info(String.valueOf(decoratedResult));

        assertThat(String.valueOf(decoratedResult).contains("Hello World!")).isTrue();

    }



}
