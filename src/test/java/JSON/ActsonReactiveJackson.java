package JSON;


import de.undercouch.actson.JsonEvent;
import de.undercouch.actson.JsonParser;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

public class ActsonReactiveJackson {

   @Test
   public void BasicReferenceTest() {

        // Sample JSON text to parse
        byte[] json = "{\"name\":\"Elvis\"}".getBytes(StandardCharsets.UTF_8);

        JsonParser parser = new JsonParser(StandardCharsets.UTF_8);

        int pos = 0;        // position in the input JSON text
        int event;          // event returned by the parser

        do {
            // feed the parser until it returns a new event
            while ((event = parser.nextEvent()) == JsonEvent.NEED_MORE_INPUT) {
                // provide the parser with more input
                pos += parser.getFeeder().feed(json, pos, json.length - pos);

                // indicate end of input to the parser
                if (pos == json.length) {
                    parser.getFeeder().done();
                }
            }

            // handle event
            System.out.println("JSON event: " + event);
            if (event == JsonEvent.ERROR) {
                throw new IllegalStateException("Syntax error in JSON text");
            }
        } while (event != JsonEvent.EOF);
   }
}
