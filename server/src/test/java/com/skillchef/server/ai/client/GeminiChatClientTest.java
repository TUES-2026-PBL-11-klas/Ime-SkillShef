package com.skillchef.server.ai.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class GeminiChatClientTest {

    private static AiProperties props() {
        AiProperties props = new AiProperties();
        props.setApiKey("test-key");
        props.setModel("gemini-2.0-flash");
        props.setBaseUrl("https://gemini.test/v1beta");
        return props;
    }

    @Test
    void mapsRequestToGeminiWireFormatAndParsesReply() {
        AiProperties props = props();
        RestClient.Builder builder = RestClient.builder().baseUrl(props.getBaseUrl());
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        server.expect(requestTo(containsString("/models/gemini-2.0-flash:generateContent")))
                .andExpect(method(POST))
                .andExpect(header("x-goog-api-key", "test-key"))
                .andExpect(jsonPath("$.systemInstruction.parts[0].text").value("You are a chef"))
                .andExpect(jsonPath("$.contents[0].role").value("user"))
                .andExpect(jsonPath("$.contents[0].parts[0].text").value("Suggest a pasta dish"))
                .andRespond(withSuccess("""
                        {
                          "candidates": [
                            { "content": { "parts": [ { "text": "Try cacio e pepe." } ], "role": "model" } }
                          ],
                          "usageMetadata": { "promptTokenCount": 12, "candidatesTokenCount": 5 }
                        }
                        """, MediaType.APPLICATION_JSON));

        GeminiChatClient client = new GeminiChatClient(props, builder.build());
        AiChatResult result = client.complete(AiChatRequest.of("You are a chef", "Suggest a pasta dish"));

        assertThat(result.content()).isEqualTo("Try cacio e pepe.");
        assertThat(result.promptTokens()).isEqualTo(12);
        assertThat(result.completionTokens()).isEqualTo(5);
        server.verify();
    }

    @Test
    void serverErrorBecomesRetryableException() {
        AiProperties props = props();
        RestClient.Builder builder = RestClient.builder().baseUrl(props.getBaseUrl());
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo(containsString("generateContent"))).andRespond(withServerError());

        GeminiChatClient client = new GeminiChatClient(props, builder.build());

        assertThatThrownBy(() -> client.complete(AiChatRequest.userPrompt("hi")))
                .isInstanceOf(AiClientException.class)
                .satisfies(e -> assertThat(((AiClientException) e).isRetryable()).isTrue());
    }

    @Test
    void clientErrorBecomesNonRetryableException() {
        AiProperties props = props();
        RestClient.Builder builder = RestClient.builder().baseUrl(props.getBaseUrl());
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo(containsString("generateContent"))).andRespond(withStatus(BAD_REQUEST));

        GeminiChatClient client = new GeminiChatClient(props, builder.build());

        assertThatThrownBy(() -> client.complete(AiChatRequest.userPrompt("hi")))
                .isInstanceOf(AiClientException.class)
                .satisfies(e -> assertThat(((AiClientException) e).isRetryable()).isFalse());
    }

    @Test
    void missingApiKeyFailsFastWithoutCallingProvider() {
        AiProperties props = props();
        props.setApiKey("  ");
        RestClient.Builder builder = RestClient.builder().baseUrl(props.getBaseUrl());
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        // No expectations registered: any HTTP call would fail the test.

        GeminiChatClient client = new GeminiChatClient(props, builder.build());

        assertThatThrownBy(() -> client.complete(AiChatRequest.userPrompt("hi")))
                .isInstanceOf(AiClientException.class)
                .hasMessageContaining("not configured");
        server.verify();
    }
}
