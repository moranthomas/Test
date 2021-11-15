package Streams;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.IntStream;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ParallelStreams {

    /* SequentialToParallelTests */

    @Test
    public void sequentialStreamOf() {
        assertFalse(Stream.of(3, 1, 4, 1, 5, 9).isParallel());
    }

    @Test
    public void sequentialIterateStream() {
        assertFalse(Stream.iterate(1, n -> n + 1).isParallel());
    }

    @Test
    public void sequentialGenerateStream() {
        assertFalse(Stream.generate(Math::random).isParallel());
    }

    @Test
    public void sequentialCollectionStream() {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9);
        assertFalse(numbers.stream().isParallel());
    }

    @Test
    public void parallelMethodOnStream() {
        assertTrue(Stream.of(3, 1, 4, 1, 5, 9)
            .parallel()
            .isParallel());
    }

    @Test
    public void parallelStreamMethodOnCollection() {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9);
        assertTrue(numbers.parallelStream().isParallel());
    }

    @Test
    public void parallelStreamThenSequential() {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9);
        assertFalse(numbers.parallelStream()
            .sequential()
            .isParallel());
    }

    @Test
    public void switchingParallelToSequentialInSameStream() {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9);
        List<Integer> nums = numbers.parallelStream()
            .map(n -> n * 2)
            .peek(n -> System.out.printf("%s processing %d%n", Thread.currentThread().getName(), n))
            .sequential()
            .sorted()
            .collect(Collectors.toList());
        System.out.println(nums);
    }

    @Test
    public void simulateWorkloadToShowMultipleThreads() {
        //The above tests will almost always just execute on main so we introduce some fake time wait to see the tasks exec
        Stream.of(1, 2).parallel()
            .peek(x -> System.out.println("processing "+x+" in "+Thread.currentThread()))
            .map(x -> {
                LockSupport.parkNanos("simulated workload", TimeUnit.SECONDS.toNanos(2));
                return x;
            })
            .forEach(System.out::println);
    }

    /* Using reduce() */
    @Test
    public void sumNumbersInList() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
        int result = numbers.stream().reduce(0, Integer::sum);
        assertTrue(result == 21);
    }

    @Test
    public void joinCharactersIntoString() {
        List<String> letters = Arrays.asList("a", "b", "c", "d", "e");
        String result = letters.stream().reduce("", String::concat);
        assertTrue(result.equalsIgnoreCase("abcde"));
    }

    @Test
    public void testParallelStreamNoFlatMap() {
        List<List<String>> listOfLists = Arrays.asList(
            Arrays.asList("one", "two"),
            Arrays.asList("five", "six"),
            Arrays.asList("three", "four")
        );
        listOfLists.parallelStream()
            .forEach(System.out::println);
    }

    @Test
    public void streamFlatMapExample() {
        List<List<String>> listOfLists = Arrays.asList(
            Arrays.asList("one", "two"),
            Arrays.asList("five", "six"),
            Arrays.asList("three", "four")
        );

        List<String> result = listOfLists.stream()
            .flatMap(childList -> childList.stream())
            .collect(Collectors.toList());
        System.out.println(result);
    }

    @Test
    public void parallelStreamFlatMapExample() {
        List<List<String>> listOfLists = Arrays.asList(
            Arrays.asList("one", "two"),
            Arrays.asList("five", "six"),
            Arrays.asList("three", "four")
        );
        List<String> result = listOfLists
            .parallelStream()
            .flatMap(childList -> childList.stream())
            .collect(Collectors.toList());
        System.out.println(result);
    }

}