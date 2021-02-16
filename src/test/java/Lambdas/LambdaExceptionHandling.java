package Lambdas;

import java.util.function.BiConsumer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.converter.GsonMessageConverter;

@Slf4j
public class LambdaExceptionHandling {

    int [] someNumbers = {1,2,3,4,5,6,7,8,9};
    int key =0;

    @Test
    public void testLambda () {
        try {
            process(someNumbers, key, (v,k) -> log.info(String.valueOf(v/ k)));
        }
        catch(ArithmeticException ae) {
            log.error("Whoops! Div by zero! " +ae);
        }
    };

        private static void process(int[] someNumbers, int key, BiConsumer<Integer, Integer> consumer) {
            for (int i : someNumbers) {
                consumer.accept(i, key);
            }
        }
}
