package pl.square;

import org.hamcrest.CoreMatchers;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.containsString;

@ActiveProfiles("mock")
@SpringBootTest
@AutoConfigureWebTestClient
class AttributesDemoApplicationTest {

    /** queries used */
    public static final String LANGUAGES_QUERY = "{languages{code}}";
    public static final String SERVICE_SDL_QUERY = "{_service{sdl}}";
    public static final String ATTRIBUTES_QUERY = "{ attributes { " +
            " name, labels { language { code }, label } " +
                "} }";
    public static final String VALUES_QUERY = "{ attributeValues { " +
            "  attribute { name }, value, sortOrder,localizedValues { " +
            "   language { code }, value " +
            "  } } " +
            "}";

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void willProvideInterfaceDefinitionUsingSDL() {

         webTestClient.get()
                 .uri(prepareUrlForQuery(SERVICE_SDL_QUERY))
                 .accept(MediaType.APPLICATION_JSON)
                 .exchange()
                 .expectStatus().isOk()
                 .expectBody(String.class).consumeWith(
                         body -> body.toString().contains("LocalizedLabel"));

    }


    @Test
    public void willReturnMockLanguages() {

        webTestClient.get()
                .uri(prepareUrlForQuery(LANGUAGES_QUERY))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(body -> {
                    Assertions.assertTrue(body.getResponseBody().contains("pl"));
                        });

    }

    @Test
    public void willReturnMockAttributes() {

        webTestClient.get()
                .uri(prepareUrlForQuery(ATTRIBUTES_QUERY))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(CoreMatchers.containsString("Tagliecasco"));

    }


    @Test
    public void willReturnMockAttributeValues() {

        webTestClient.get()
                .uri(prepareUrlForQuery(VALUES_QUERY))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(containsString("35mm"));

    }

    @Test
    public void allowsAddingNewLaguages() {

        //add
        String query = "mutation addLanguage { addLanguage(code:\"pt_BR\")}";
        webTestClient.get()
                .uri(prepareUrlForQuery(query))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(containsString("SUCCESS"));

        // verify
        webTestClient.get()
                .uri(prepareUrlForQuery(LANGUAGES_QUERY))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(body -> {
                    Assertions.assertTrue(body.getResponseBody().contains("pt_BR"));
                });
    }

    @Test
    public void allowsAddingNewAttributes() {

        //add
        String query = "mutation addAttribute { " +
                " addAttribute(attr: {name: \"width\", labels: [{label: \"szerokosc\", language: {code: \"pl_PL\"}},\n" +
                " {label: \"Breite\", language: {code: \"de_DE\"}}] })}";
        webTestClient.get()
                .uri(prepareUrlForQuery(query))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(containsString("SUCCESS"));

        // verify
        webTestClient.get()
                .uri(prepareUrlForQuery(ATTRIBUTES_QUERY))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(body -> {
                    Assertions.assertTrue(body.getResponseBody().contains("Breite"));
                });
    }

    @Test
    public void allowsAddingNewValues() {

        //add
        String query = "mutation addAttributeValue { " +
                "  addAttributeValue( value: \"green\", attributeCode: \"color\"," +
                "    localizedValues: [{language: {code: \"pl_PL\"}, value: \"zielony\"}]," +
                "    sortOrder: 30" +
                " )}";
        webTestClient.get()
                .uri(prepareUrlForQuery(query))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(containsString("SUCCESS"));

        // verify
        webTestClient.get()
                .uri(prepareUrlForQuery(VALUES_QUERY))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).consumeWith(body -> {
                    Assertions.assertTrue(body.getResponseBody().contains("zielony"));
                });
    }

    // --------------------------------------

    @NotNull
    private URI prepareUrlForQuery(String query) {

        try {
            return new URI("/graphql/?query=" + URLEncoder.encode(query, UTF_8));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }



}
