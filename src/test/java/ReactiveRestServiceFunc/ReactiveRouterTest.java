package ReactiveRestServiceFunc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
//  We create a `@SpringBootTest`, starting an actual server on a `RANDOM_PORT`
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReactiveRouterTest {

    // Spring Boot will create a `WebTestClient` for you,
    // already configure and ready to issue requests against "localhost:RANDOM_PORT"
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testHello() {
        webTestClient
            // Create a GET request to test an endpoint
            .get().uri("/hello")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Greeting.class).value(greeting -> {
            assertThat(greeting.getMessage()).isEqualTo("Hello, Spring!");
        });
    }

    @Test
    public void testGoodbye() {
        webTestClient
            // Create a GET request to test an endpoint
            .get().uri("/goodbye")
            .header("Content-Type", "text/plain")           // its not using this :(
            .accept(MediaType.APPLICATION_JSON)             // but it is using this - if you include it only it will enforce however
            .exchange()
            .expectStatus().isOk()
            .expectBody(Greeting.class).value(
                greeting -> {
                    assertThat(greeting.getMessage()).isEqualTo("Goodbye, Spring!");
                }
            );
    }

    @Test
    public void testGoodbye2() {
        webTestClient
            // Create a GET request to test an endpoint
            .get().uri("/goodbye2Params")
            .header("Content-Type", "text/plain")           // its not using this :(
            .accept(MediaType.APPLICATION_JSON)             // but it is using this - if you include it only it will enforce however
            .exchange()
            .expectStatus().isOk()
            .expectBody(Greeting.class).value(
            greeting -> {
                assertThat(greeting.getMessage()).isEqualTo("Goodbye, Spring!");
            }
        );
    }


    @Test
    void testHelloBadMedia() {
        webTestClient.get().uri("/hello").accept(MediaType.TEXT_PLAIN).exchange()
            .expectStatus().is4xxClientError();
    }


}