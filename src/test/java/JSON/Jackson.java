package JSON;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;


public  class Jackson {

        @Test
        public void JsonToJavaUtils() throws JsonProcessingException {

            // JSON string to Java Object  ... using readValue()
            ObjectMapper mapper = new ObjectMapper();

            String json ="["
                + "  {"
                + "    \"id\": 1,"
                + "    \"name\": {"
                + "      \"first\": \"Yong\","
                + "      \"last\": \"Mook Kim\""
                + "    },"
                + "    \"contact\": ["
                + "      {"
                + "        \"type\": \"phone/home\","
                + "        \"ref\": \"111-111-1234\""
                + "      },"
                + "      {"
                + "        \"type\": \"phone/work\","
                + "        \"ref\": \"222-222-2222\""
                + "      }"
                + "    ]"
                + "  },"
                + "  {"
                + "    \"id\": 2,"
                + "    \"name\": {"
                + "      \"first\": \"Tom\","
                + "      \"last\": \"Moran\""
                + "    },"
                + "    \"contact\": ["
                + "      {"
                + "        \"type\": \"phone/home\","
                + "        \"ref\": \"333-333-1234\""
                + "      },"
                + "      {"
                + "        \"type\": \"phone/work\","
                + "        \"ref\": \"444-444-4444\""
                + "      }"
                + "    ]"
                + "  }"
                + "]";


            Object obj = mapper.readValue(json, Object.class );

            System.out.println("THE OBJECT = "+ obj );

            // Java objects to JSON string - compact-print
            String jsonString = mapper.writeValueAsString(obj);
            //System.out.println(jsonString);

            // Java objects to JSON string - pretty-print ( much better :) )
            String jsonInString2 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            System.out.println(jsonInString2);

            JsonNode rootArray = mapper.readTree(json);

            for (JsonNode root : rootArray) {

                // Get id
                long id = root.path("id").asLong();
                System.out.println("id : " + id);

                // Get Name
                JsonNode nameNode = root.path("name");
                if (!nameNode.isMissingNode()) {
                    System.out.println(nameNode.path("first").asText());
                }

                JsonNode contactNode = root.path("contact");
                if (contactNode.isArray()) {
                    System.out.println("Is this " + contactNode+ " node an Array? "
                        + contactNode.isArray());
                }
            }
        }
}

