package Strings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.runners.VerboseMockitoJUnitRunner;

@Slf4j
class StringsTest {

    @BeforeEach
    void setUp() {
    }

    String INPUTSTRING = "The brown fox jumped over the lazy dog";
    String REVERSED = "god yzal eht revo depmuj xof nworb ehT";
    String REVERSED_WORDS_ONLY = "ehT nworb xof depmuj revo eht yzal god ";
    String VULNERABLESQL = "SELECT account_balance FROM user_data WHERE user_name = tom' or '1'='1";

    @Test
    void reversingUsingStringBuilder() {
        Strings strings = new Strings();
        String rev = strings.reverseUsingStringBuilder(INPUTSTRING);
        assertThat(rev).isEqualTo(REVERSED_WORDS_ONLY);
    }

    @Test
    void reversingUsingJava8() {
    }

    @Test
    void reversingUsingStringBuilderReverse() {
    }

    @Test
    void testRegexForSqlInjection() {

        final String[] blackListedCharVals={"[", "-", "]", "#", "*", "%", "=", "!", ">", "<", "'"};

        Optional<String> result = stringContainsItemFromList(VULNERABLESQL, blackListedCharVals);
        String dirty = null;
        while(result.isPresent()) {
            dirty = result.get();
            VULNERABLESQL = VULNERABLESQL.replaceAll(dirty, "");
            result = stringContainsItemFromList(VULNERABLESQL, blackListedCharVals);
        }
    }

    public static Optional<String> stringContainsItemFromList(String inputStr, String[] items) {
        return Arrays.stream(items).filter(inputStr::contains).findFirst();
    }

}