package com.skillchef.server.storage;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Serves files written by {@link LocalStorageService} over HTTP at
 * {@code /uploads/**}. Replaced by CDN URLs once cloud storage is wired in.
 */
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
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
