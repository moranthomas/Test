package Lambdas;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class LambdaBasics{

    @Test
    public void test1() {

        MyStringLambda hello = () -> log.info("Hi from MyStringLambda!");
        FooLambdaInterface fooLambdaInterface = () -> log.info("Hi from FooLambdaInterface");

        hello.logMeOut();
        fooLambdaInterface.perform();

        FooLambdaInterface anonInnerClass = new FooLambdaInterface() {
            @Override
            public void perform() {
                log.info("Hi from AnonInnerClass");
            }
        };

        anonInnerClass.perform();

        /** Doesn't work **/
        //assertThat(aBlockOfCode.logMeOut()).isEqualTo("Hello World!");

        SquareLambda squareLambda = (int a) -> a * a;
        assertThat(squareLambda.squared(2)).isEqualTo(4);

        //StringLengthLamba stringLengthLamba = (String s) -> s.length();
        StringLengthLambda stringLengthLamba = s -> s.length();
        log.info("length is: " + stringLengthLamba.getLength("A very long string"));

        printLength(s -> s.length());

    }

    @Test
    public void runnableAsInnerClass() {
        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("Printed inside runnable");
            }
        });

        myThread.run();
    }

    @Test
    public void runnableAsLambda() {
        Thread myThread = new Thread(() -> log.info("Printed inside lambda runnable"));
        myThread.run();
    }


    public void printLength(StringLengthLambda lengthLamba) {
        log.info(String.valueOf(lengthLamba.getLength("Howlongami")));
    }


    @Test
    public void testIntegerListLambda() {
        List<Integer> naturalNumbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13);

        List<Integer> doubles = naturalNumbers.stream()
            .map(n -> {
                Integer nr = n * 2;
                return nr;
            })
            .collect(Collectors.toList());

        log.info("doubles is: " + doubles.toString());
    }

    /**************** INTERFACE DEFINITIONS ********************/

    interface MyStringLambda {
        void logMeOut();
    }

    interface SquareLambda {
        int squared(int x);
    }

    interface StringLengthLambda {
        int getLength(String s);
    }

    /** Examples of using the FUNCTIONAL class  *****/
    // Adding an element to a given list.
    BiConsumer<List, Integer> addIntoList = (List list, Integer element) -> list.add(element);

    // Generate a unique number with the help of generator.
    Supplier<Integer> uniqueKey = () -> new Random().nextInt();


}
