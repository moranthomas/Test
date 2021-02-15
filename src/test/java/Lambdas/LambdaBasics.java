package Lambdas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    interface MyStringLambda {
        void logMeOut();
    }

    interface SquareLambda {
        int squared(int x);
    }

    interface StringLengthLambda {
        int getLength(String s);
    }

}
