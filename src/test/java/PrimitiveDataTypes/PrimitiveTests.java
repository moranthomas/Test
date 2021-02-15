package PrimitiveDataTypes;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class PrimitiveTests {

    @Test
    public void charTest() {
        char xi = '\u559C';
        char one = '\u580D';
        char two = '\u581A';
        char zhe = '\u0497';
        char zheBig = '\u0496';
        log.info(String.valueOf(xi));
        log.info(String.valueOf(one));
        log.info(String.valueOf(two));
        log.info(String.valueOf(zhe));
        log.info(String.valueOf(zheBig));
    }
}
