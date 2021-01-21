package ApacheCommons;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ImmutablePairTests {

    private static ImmutablePair<String, String> immutablePair = new ImmutablePair<>("leftElement", "rightElement");

    @Test
    public void whenCalledgetLeft_thenCorrect() {
        assertThat(immutablePair.getLeft()).isEqualTo("leftElement");
    }

    @Test
    public void whenCalledgetRight_thenCorrect() {
        assertThat(immutablePair.getRight()).isEqualTo("rightElement");
    }

    @Test
    public void whenCalledof_thenCorrect() {
        assertThat(ImmutablePair.of("leftElement", "rightElement"))
                .isInstanceOf(ImmutablePair.class);
    }
    @Test
    public void whenCalledSetValue_thenThrowUnsupportedOperationException() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> {
                    immutablePair.setValue("newValue");
                });
    }
    
    @Test
    public void testThatKeyAndValuesAreStrings() {
        //note they can be Integer or anything else as well.
        assertThat(immutablePair.getKey()).isInstanceOf(String.class);
        assertThat(immutablePair.getValue()).isInstanceOf(String.class);
    }

}
