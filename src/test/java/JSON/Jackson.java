package JSON;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;


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

    private String getJSONFromFileApacheCommonsIO() {

        String jsonMessage = null;
        try {
            File file = ResourceUtils.getFile("classpath:sample_token.json");
            jsonMessage = FileUtils.readFileToString(file, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonMessage;
    }


    /*
    REQUIRES JAVA 11 - LEAVING HERE FOR REFERENCE
    private String getJSONFromFileJava11() {
    String jsonContent =n ull;
        try {
            Path jsonFile = Paths.get("src/test/resources/sample_token.json");
            String jsonContent = Files.readString(jsonFile);
            return jsonContent;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }*/

    private String getJsonContentJava8(String name) throws IOException {
        File file =
            new File(Objects.requireNonNull(getClass().getClassLoader().getResource(name)).getFile());
        return String.join("", Files.readAllLines(file.toPath()));
    }
}

