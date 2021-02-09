package Reactive;
import java.util.*;
import java.time.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class SimpleMonoAndFluxTests {


    /**
     * Learn how to create Flux instances.
     * **/
    // Return an empty Flux
    Flux<String> emptyFlux() {
        return Flux.empty();
    }

    @Test
    public void emptyFluxTest() {
        assertThat(emptyFlux()).isEqualTo(Flux.empty());
    }

    // Return a Flux that contains 2 values "foo" and "bar" without using an array or a collection
    Flux<String> fooBarFluxFromValues() {
        return Flux.just("foo", "bar");
    }

    @Test
    public void fooBarFluxFromValuesTest() {
        Mono<List<String>> list = fooBarFluxFromValues().collectList();

        Mono<String> upList = list.log().map( x -> x.get(0).toUpperCase());
        upList.log().subscribe();
        //assertThat(upList.doOnNext().equals("FOO"));
    }


    // Create a Flux from a List that contains 2 values "foo" and "bar"
    Flux<String> fooBarFluxFromList() {
        ArrayList<String> a = new ArrayList<>(Arrays.asList("foo", "bar"));
        return Flux.fromIterable(a);
    }

    // Create a Flux that emits an IllegalStateException
    Flux<String> errorFlux() {
        return Flux.error(new IllegalStateException());
    }

    // Create a Flux that emits increasing values from 0 to 9 each 100ms
    Flux<Long> counter() {
        Duration period = Duration.ofSeconds(1);
        return Flux.interval(period).take(10);
    }

    /**********************************************************************************************/

    //Return an empty Mono
    Mono<String> emptyMono() {
        return Mono.empty();
    }


    @Test
    public void empty() {
        Mono<String> mono = this.emptyMono();
        StepVerifier.create(mono)
            .verifyComplete();
    }

    // Return a Mono that never emits any signal
    Mono<String> monoWithNoSignal() {
        return Mono.never();
    }

    @Test
    public void noSignal() {
        Mono<String> mono = this.monoWithNoSignal();
        StepVerifier
            .create(mono)
            .expectSubscription()
            .expectTimeout(Duration.ofSeconds(1))
            .verify();
    }

    // Return a Mono that contains a "foo" value
    Mono<String> fooMono() {
        return Mono.just("foo");
    }

    @Test
    public void fromValue() {
        Mono<String> mono = this.fooMono();
        StepVerifier.create(mono)
            .expectNext("foo")
            .verifyComplete();
    }


    // Create a Mono that emits an IllegalStateException
    Mono<String> errorMono() {
        return Mono.error(new IllegalStateException());
    }

    @Test
    public void error() {
        Mono<String> mono = this.errorMono();
        StepVerifier.create(mono)
            .verifyError(IllegalStateException.class);
    }





}

