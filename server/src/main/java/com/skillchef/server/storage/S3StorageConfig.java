package com.skillchef.server.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

/**
 * Builds the {@link S3Client} used by {@link S3StorageService}. Only created when
 * {@code skillchef.storage.provider=s3} (the default outside of development), so the
 * AWS SDK is never initialised when running on the local-filesystem backend.
 *
 * <p>Works against both AWS S3 (leave {@code endpoint} blank) and S3-compatible
 * stores such as Cloudflare R2 (set {@code endpoint} to the account R2 URL and
 * {@code region} to {@code auto}).
 */
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
@ConditionalOnProperty(name = "skillchef.storage.provider", havingValue = "s3", matchIfMissing = true)
public class S3StorageConfig {

    @Bean
    public S3Client s3Client(StorageProperties properties) {
        StorageProperties.S3 s3 = properties.getS3();

        S3ClientBuilder builder = S3Client.builder()
                .region(Region.of(s3.getRegion()))
                .credentialsProvider(credentialsProvider(s3))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(s3.isPathStyleAccess())
                        .build());

        if (StringUtils.hasText(s3.getEndpoint())) {
            builder.endpointOverride(URI.create(s3.getEndpoint()));
        }

        return builder.build();
    }

    /**
     * Uses explicit keys when both are configured; otherwise falls back to the
     * default AWS credentials chain (env vars, instance profile, etc.).
     */
    private static software.amazon.awssdk.auth.credentials.AwsCredentialsProvider credentialsProvider(
            StorageProperties.S3 s3) {
        if (StringUtils.hasText(s3.getAccessKeyId()) && StringUtils.hasText(s3.getSecretAccessKey())) {
            return StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(s3.getAccessKeyId(), s3.getSecretAccessKey()));
        }
        return DefaultCredentialsProvider.create();
    }
}
