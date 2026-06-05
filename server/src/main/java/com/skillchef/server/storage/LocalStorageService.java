package com.skillchef.server.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Local-filesystem {@link StorageService} used in development (and any environment
 * where {@code skillchef.storage.provider=local}). Files are written under
 * {@code skillchef.storage.local-dir} and served back via a static resource handler
 * (see {@link StorageWebConfig}). The cloud-backed {@link S3StorageService} takes
 * over when {@code skillchef.storage.provider=s3}.
 */
@Service
@EnableConfigurationProperties(StorageProperties.class)
@ConditionalOnProperty(name = "skillchef.storage.provider", havingValue = "local")
public class LocalStorageService extends AbstractStorageService {

    public LocalStorageService(StorageProperties properties) {
        super(properties);
    }

    @Override
    public String store(String folder, MultipartFile file) {
        validateImage(file);

        String key = generateKey(file);
        Path folderPath = Paths.get(properties.getLocalDir(), folder).toAbsolutePath().normalize();

        try {
            Files.createDirectories(folderPath);
            Path target = folderPath.resolve(key);
            file.transferTo(target);
        } catch (IOException ex) {
            throw StorageException.internal("Failed to store uploaded file");
        }

        return joinUrl(properties.getPublicBaseUrl(), folder, key);
    }
}
