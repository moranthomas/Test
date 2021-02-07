package Reactive;
import java.util.*;
import java.time.*;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleFluxTests {


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
            //assertThat(list.flatMap( x -> x.get(0))).isEqualTo("foo");
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



}

