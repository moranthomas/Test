package Reactive;

import java.time.Duration;
import java.util.function.Supplier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class StepVerifierTests {

    // Use StepVerifier to check that the flux parameter emits "foo" and "bar" elements then completes successfully.
    void expectFooBarComplete(Flux<String> flux) {
        StepVerifier.create(flux).expectNext("foo","bar").verifyComplete();
    }
    @Test
    public void expectElementsThenComplete() {
        expectFooBarComplete(Flux.just("foo", "bar"));
    }

    // Use StepVerifier to check that the flux parameter emits "foo" and "bar" elements then a RuntimeException error.
    void expectFooBarError(Flux<String> flux) {
        StepVerifier.create(flux)
            .expectNext("foo", "bar")
            .verifyError(RuntimeException.class);
    }

    // Use StepVerifier to check that the flux parameter emits a User with "swhite"username then completes successfully.
    void expectSnowWhiteComplete(Flux<User> flux) {
        StepVerifier.create(flux)
            .assertNext(t -> Assertions.assertThat(t.userName).isEqualTo("swhite"));
    }
    @Test
    public void expectElementsWithThenComplete() {
        expectSnowWhiteComplete(Flux.just(new User("swhite", 1), new User("jpinkman", 2)));
    }

    void expect10Elements(Flux<Long> flux) {
        StepVerifier.create(flux).expectNextCount(10).verifyComplete();
    }
    @Test
    public void count() {
        expect10Elements(Flux.interval(Duration.ofSeconds(1)).take(10));
    }

    // Expect 3600 elements at intervals of 1 second, and verify quicker than 3600s by manipulating virtual time
    void expect3600Elements(Supplier<Flux<Long>> supplier) {
        StepVerifier.withVirtualTime(supplier).expectSubscription()
            .expectNoEvent(Duration.ofSeconds(1))
            .thenAwait(Duration.ofHours(1))
            .expectNextCount(10)
            .expectComplete()
            .verify();
    }
    @Test
    public void countWithVirtualTime() {
        expect3600Elements(() -> Flux.interval(Duration.ofSeconds(1)).take(3600));
    }


    private static class User {

        String userName;
        int userId;

        public User(String userName, int userId) {
            this.userName = userName;
            this.userId = userId;
        }
    }

}
