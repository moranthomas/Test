package JSON;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;


public  class Jackson {

        @Test
        public void JsonToJavaObject() throws JsonProcessingException {

            // JSON string to Java Object  ... using readValue()
            ObjectMapper mapper = new ObjectMapper();

            String s="{\"coord\":{\"lon\":-80.25,\"lat\":43.55},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"Sky is Clear\"}]}";
            Object obj = mapper.readValue(s, Object.class );

            System.out.println("THE OBJECT = "+ obj );

            // Java objects to JSON string - compact-print
            String jsonString = mapper.writeValueAsString(obj);
            System.out.println(jsonString);

            // Java objects to JSON string - pretty-print ( much better :) )
            String jsonInString2 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            System.out.println(jsonInString2);

        }
}

