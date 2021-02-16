package Lambdas;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class StreamsAndLambdas {

    static List<Person> people;

    @BeforeAll
    public static void setupPersons() {
        people  = Arrays.asList(
            new Person("Charles", "Dickens", 60),
            new Person("Lewis", "Carroll", 42),
            new Person("Thomas", "Carlyle", 51),
            new Person("Charlotte", "Bronte", 45),
            new Person("Matthew", "Arnold", 39)
        );
    }

    @Test
    public void testStream() {
        people.stream()
            .filter( p-> p.getLastName().startsWith("C") )
            .forEach(System.out::println);
    }

    @Test
    public void testExamples() {

        // 1. Integer Stream
        IntStream
            .range(1, 10)
            .skip(5)
            .forEach(System.out::println);

        // 2. Stream.of, sorted and findFirst
        Stream.of("Ava", "Aneri", "Alberto")
            .sorted()
            .findFirst()
            .ifPresent(System.out::println);

        // 3. Stream from Array, sort, filter and print
        String[] names = {"Ava", "Aneri", "Alberto", "Barry", "Steve", "Sally", "Sue" };
        Arrays.stream(names)
            .filter(x -> x.startsWith("S"))
            .sorted()
            .forEach(System.out::println);

        // 4. Average of squares of an int array
        Arrays.stream(new int[] {2,4,6,8,10})
            .map(x -> x*x)
            .average()
            .ifPresent(System.out::println);

        // 5. Stream from List, filter and print
        List<String> people = Arrays.asList("Ava", "Aneri", "Alberto", "Barry", "Steve", "Sally", "Sue" );
        people
            .stream()
            .map(String::toLowerCase)
            .filter(x -> x.startsWith("a"))
            .forEach(System.out::println);
    }




}
