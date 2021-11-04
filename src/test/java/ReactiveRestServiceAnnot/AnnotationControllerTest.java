package ReactiveRestServiceAnnot;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AnnotationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testGoodbye2() {
        webTestClient
            // Create a GET request to test an endpoint
            .get().uri("/updateUserById")
            .header("Content-Type", "text/plain")           // its not using this :(
            .accept(MediaType.APPLICATION_JSON)             // but it is using this - if you include it only it will enforce however
            .exchange()
            .expectStatus().isOk()
            .expectBody(Greeting.class).value(
                greeting -> {
                    assertThat(greeting.getMessage()).isEqualTo("User, Updated!");
                 }
             );
    }

    @Test
    public void testAll() {
        webTestClient
            // Create a GET request to test an endpoint
            .get().uri("/all")
            .header("Content-Type", "text/plain")           // its not using this :(
            .accept(MediaType.APPLICATION_JSON)             // but it is using this - if you include it only it will enforce however
            .exchange()
            .expectStatus().isOk();
    }

    // -- Find BankAccountDetails entities with balance 1000
   /* webClient.get().uri("/findByBalance/{balance}",
        1000).accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToFlux(BankAccountDetails.class)
        .subscribe(account -> log.info("account with balance 1000 -> " + account.getAccountId()));*/


    @Test
    public void testPutUpdateUserByUserId() {
        String dummyBody = "{ \"fakeBody\": \"some stuff to send\" }";

        webTestClient
            // Create a GET request to test an endpoint
            .post().uri("/updateUserById/{userId}", 1000)
            .header("Content-Type", "application/json")       // it DOES use this for put/post
            //.accept(MediaType.APPLICATION_JSON)             // it is using this to enforce the media type on the response
            //.bodyValue(dummyBody)
            .body(Mono.just(dummyBody), String.class)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Greeting.class).value(
            greeting -> {
                assertThat(greeting.getMessage()).isEqualTo("User, Updated!");
            }

            //erverSentEventHttpMessageReader only supports reading stream of events as a Flux
        );;
    }
}