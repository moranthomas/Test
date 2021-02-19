package CircuitBreaker;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.junit.Test;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.Function;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.*;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Resilience4JTests {

    interface RemoteService {
        int process(int i);
    }

    // Functional Interface - Only one abstract method.
    interface HelloWorldService {
        abstract String sayHelloWorld();
    }

    private RemoteService service;
    private HelloWorldService helloWorldService;


    @SuppressWarnings("unchecked")
    @Test
    public void whenTimeLimitIsUnder_thenItWorksAsExpected() throws Exception {

        // Get or create a TimeLimiter from the registry, using a custom configuration when creating the TimeLimiter
        TimeLimiterConfig config = TimeLimiterConfig.custom()
            .cancelRunningFuture(false)
            .timeoutDuration(Duration.ofMillis(500))
            .build();

        // Create a TimeLimiterRegistry with a custom global configuration
        TimeLimiterRegistry timeLimiterRegistry = TimeLimiterRegistry.of(config);

        TimeLimiter timeLimiterWithCustomConfig = timeLimiterRegistry.timeLimiter("rabbitTimeLimiter", config);


        // Given I have a helloWorldService.sayHelloWorld() method which doesn't take too long (no artificial wait)
        HelloWorldService helloWorldService = new HelloWorldService() {
            @Override
            public String sayHelloWorld() {
                String str = "Hello Worlds!!!";
                return str;
            }
        };

        // Create a TimeLimiter
        TimeLimiter timeLimiter = TimeLimiter.of(Duration.ofSeconds(1));

        // The Scheduler is needed to schedule a timeout on a non-blocking CompletableFuture
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

        // The non-blocking variant with a CompletableFuture
        CompletableFuture<String> result = timeLimiter.executeCompletionStage(
            scheduler, () -> CompletableFuture.supplyAsync(helloWorldService::sayHelloWorld)).toCompletableFuture();

        try{
            //Wait for it to complete normally
            Thread.sleep(1000);
        }
        catch (InterruptedException ie) {
            log.error(String.valueOf(ie));
        }
        log.info(String.valueOf(result));

        assertThat(String.valueOf(result).contains("Completed normally")).isTrue();

    }


    @SuppressWarnings("unchecked")
    @Test
    public void whenTimeLimitIsOver_thenItFailsAsExpected() throws Exception {

        // Get or create a TimeLimiter from the registry, using a custom configuration when creating the TimeLimiter
        TimeLimiterConfig config = TimeLimiterConfig.custom()
            .cancelRunningFuture(false)
            .timeoutDuration(Duration.ofMillis(500))
            .build();

        // Create a TimeLimiterRegistry with a custom global configuration
        TimeLimiterRegistry timeLimiterRegistry = TimeLimiterRegistry.of(config);

        TimeLimiter timeLimiterWithCustomConfig = timeLimiterRegistry.timeLimiter("rabbitTimeLimiter", config);

        // Given I have a helloWorldService.sayHelloWorld() method which takes too long (artificial wait added)
        HelloWorldService helloWorldService = new HelloWorldService() {
            @Override
            public String sayHelloWorld() {
                try {
                    Thread.sleep(600);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                String str = "Hello Worlds!!!";
                return str;
            }
        };

        // Create a TimeLimiter
        TimeLimiter timeLimiter = TimeLimiter.of(Duration.ofSeconds(1));

        // The Scheduler is needed to schedule a timeout on a non-blocking CompletableFuture
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

        // The non-blocking variant with a CompletableFuture
        CompletableFuture<String> result = timeLimiter.executeCompletionStage(
            scheduler, () -> CompletableFuture.supplyAsync(helloWorldService::sayHelloWorld)).toCompletableFuture();

        log.info(String.valueOf(result));

        assertThat(String.valueOf(result).contains("Not completed")).isTrue();

    }


    @Test
    public void whenCircuitBreakerIsUsed_thenItWorksAsExpected() throws InterruptedException {

        /*RemoteService service = new RemoteService() {
            @Override
            public int process(int i)  {
                return 0;
            }
        };*/

        service = mock(RemoteService.class);
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            // Percentage of failures to start short-circuit
            .failureRateThreshold(20)
            // Min number of call attempts
            //.slidingWindow(int, int, SlidingWindowType)
            .ringBufferSizeInClosedState(5)
            .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        CircuitBreaker circuitBreaker = registry.circuitBreaker("rabbitService");

        Function<Integer, Integer> decorated;
        decorated = CircuitBreaker.decorateFunction(circuitBreaker, service::process);

        when(service.process(anyInt())).thenThrow(new RuntimeException());

        for (int i = 0; i < 10; i++) {
            try {
                decorated.apply(i);
            } catch (Exception ignore) {
            }
        }

        verify(service, times(5)).process(any(Integer.class));
    }


    private Future<?> callAndBlock(Function<Integer, Integer> decoratedService) throws InterruptedException {

        /*RemoteService service = new RemoteService() {
            @Override
            public int process(int i)  {
                return 0;
            }
        };*/
        service = mock(RemoteService.class);

        CountDownLatch latch = new CountDownLatch(1);
        when(service.process(anyInt())).thenAnswer(invocation -> {
            latch.countDown();
            Thread.currentThread().join();
            return null;
        });

        ForkJoinTask<?> result = ForkJoinPool.commonPool().submit(() -> {
            decoratedService.apply(1);
        });
        latch.await();
        return result;
    }

    @Test
    public void whenRetryIsUsed_thenItWorksAsExpected() {

        /*RemoteService service = new RemoteService() {
            @Override
            public int process(int i)  {
                return 0;
            }
        };*/

        service = mock(RemoteService.class);

        RetryConfig config = RetryConfig.custom().maxAttempts(2).build();
        RetryRegistry registry = RetryRegistry.of(config);
        Retry retry = registry.retry("rabbitService");
        Function<Integer, Void> decorated = Retry.decorateFunction(retry, (Integer s) -> {
            service.process(s);
            return null;
        });

        when(service.process(anyInt())).thenThrow(new RuntimeException());

        try {
            decorated.apply(1);
            fail("Expected an exception to be thrown if all retries failed");
        } catch (Exception e) {
            verify(service, times(2)).process(any(Integer.class));
        }
    }


}
