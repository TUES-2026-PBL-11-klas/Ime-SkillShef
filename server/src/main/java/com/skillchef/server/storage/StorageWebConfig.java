package com.skillchef.server.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Serves files written by {@link LocalStorageService} over HTTP at
 * {@code /uploads/**}. Only registered for the local provider; the S3 / R2
 * backend serves objects via its own CDN URLs instead.
 */
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
@ConditionalOnProperty(name = "skillchef.storage.provider", havingValue = "local")
public class StorageWebConfig implements WebMvcConfigurer {

    private final StorageProperties properties;

    public StorageWebConfig(StorageProperties properties) {
        this.properties = properties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Paths.get(properties.getLocalDir()).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}
