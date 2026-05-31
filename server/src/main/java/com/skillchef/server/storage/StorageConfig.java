package com.skillchef.server.storage;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/** Wires the AWS SDK v2 S3 client and presigner beans. Compatible with both AWS S3 and Cloudflare R2. */
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StorageConfig {

    @Bean
    public S3Client s3Client(StorageProperties props) {
        var creds = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(props.getAccessKeyId(), props.getSecretAccessKey()));

        var builder = S3Client.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(creds);

        if (!props.getEndpoint().isBlank()) {
            // Path-style is required for Cloudflare R2 and self-hosted S3-compatible stores.
            builder.endpointOverride(URI.create(props.getEndpoint()))
                   .serviceConfiguration(S3Configuration.builder()
                           .pathStyleAccessEnabled(true)
                           .build());
        }

        return builder.build();
    }

    @Bean
    public S3Presigner s3Presigner(StorageProperties props) {
        var creds = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(props.getAccessKeyId(), props.getSecretAccessKey()));

        var builder = S3Presigner.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(creds);

        if (!props.getEndpoint().isBlank()) {
            builder.endpointOverride(URI.create(props.getEndpoint()));
        }

        return builder.build();
    }
}
