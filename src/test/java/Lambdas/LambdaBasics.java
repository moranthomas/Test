package Lambdas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class LambdaBasics{

    @Test
    public void test1() {

        MyStringLambda hello = () -> log.info("Hello World!");
        FooLambdaInterface fooLambdaInterface = () -> log.info("Hi");

        hello.logMeOut();
        fooLambdaInterface.perform();

        /** Doesn't work **/
        //assertThat(aBlockOfCode.logMeOut()).isEqualTo("Hello World!");

        SquareLambda squareLambda = (int a) -> a * a;
        assertThat(squareLambda.squared(2)).isEqualTo(4);
    }


    interface MyStringLambda {
        void logMeOut();
    }

    interface SquareLambda {
        int squared(int x);
    }

}
