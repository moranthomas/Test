package Guice;

import com.google.inject.Injector;
import com.google.inject.Guice;
import java.util.Scanner;
import org.junit.Test;

public class BasicGuiceTests {

    @Test
    public void testCommunicationGuice() {
        Injector injector = Guice.createInjector(new BasicModule());
        Communication communication = injector.getInstance(Communication.class);

        String secretMessage = "Enigma code breaker";
        communication.sendMessage(secretMessage);
    }

}
