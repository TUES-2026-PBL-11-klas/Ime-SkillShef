package com.skillchef.server.ai.client;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Wires the AI chat client.
 *
 * <p>A single {@link AiChatClient} bean is exposed: the provider transport
 * (selected by {@code skillchef.ai.provider}) wrapped in {@link ResilientAiChatClient}
 * for client-side rate limiting and retry. The raw transport is intentionally not
 * a bean — callers should always go through the resilient wrapper. To add another
 * provider, extend the switch in {@link #buildTransport}.
 */
@Configuration
@EnableConfigurationProperties(AiProperties.class)
public class AiClientConfig {

    @Bean
    AiChatClient aiChatClient(AiProperties props, RestClient.Builder builder) {
        AiChatClient transport = buildTransport(props, builder);
        return new ResilientAiChatClient(transport, props);
    }

    private AiChatClient buildTransport(AiProperties props, RestClient.Builder builder) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(props.getConnectTimeout());
        factory.setReadTimeout(props.getReadTimeout());

        RestClient restClient = builder
                .baseUrl(props.getBaseUrl())
                .requestFactory(factory)
                .build();

        String provider = props.getProvider() == null ? "" : props.getProvider().toLowerCase();
        return switch (provider) {
            case "gemini" -> new GeminiChatClient(props, restClient);
            default -> throw new IllegalStateException(
                    "Unsupported skillchef.ai.provider '" + props.getProvider() + "' (supported: gemini)");
        };
    }
}
