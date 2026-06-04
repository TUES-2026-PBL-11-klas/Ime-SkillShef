package com.skillchef.server.ai.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Live smoke test that makes a REAL call to the configured AI provider.
 *
 * <p>Disabled unless the {@code AI_API_KEY} environment variable is set, so it
 * never runs in CI or for teammates without a key. Run it locally with:
 *
 * <pre>{@code
 *   # PowerShell
 *   $env:AI_API_KEY = "AIza..."        # optionally $env:AI_MODEL = "gemini-2.0-flash"
 *   ./mvnw.cmd test "-Dtest=GeminiLiveSmokeTest"
 * }</pre>
 *
 * It wires the client exactly the way {@link AiClientConfig} does (transport +
 * resilient wrapper), so a pass proves the end-to-end path works against the
 * real API.
 */
@EnabledIfEnvironmentVariable(named = "AI_API_KEY", matches = ".+")
class GeminiLiveSmokeTest {

    @Test
    void realCallReturnsAReply() {
        AiProperties props = new AiProperties();
        props.setApiKey(System.getenv("AI_API_KEY"));
        String model = System.getenv("AI_MODEL");
        if (model != null && !model.isBlank()) {
            props.setModel(model);
        }

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(props.getConnectTimeout());
        factory.setReadTimeout(props.getReadTimeout());
        RestClient restClient = RestClient.builder()
                .baseUrl(props.getBaseUrl())
                .requestFactory(factory)
                .build();

        AiChatClient client = new ResilientAiChatClient(new GeminiChatClient(props, restClient), props);

        AiChatResult result = client.complete(AiChatRequest.of(
                "You are SkillChef's culinary assistant. Reply in one short sentence.",
                "Suggest a quick dinner using eggs and spinach."));

        System.out.println("AI reply: " + result.content());
        System.out.println("tokens: prompt=" + result.promptTokens() + " completion=" + result.completionTokens());
        assertThat(result.content()).isNotBlank();
    }
}
