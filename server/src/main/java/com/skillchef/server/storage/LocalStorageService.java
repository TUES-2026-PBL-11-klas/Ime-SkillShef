package com.skillchef.server.storage;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Local-filesystem {@link StorageService} used until Person 4's cloud storage
 * SDK (S3 / Cloudflare R2 + CDN) is wired in. Files are written under
 * {@code skillchef.storage.local-dir} and served back via a static resource
 * handler (see {@code StorageWebConfig}).
 */
@Service
@EnableConfigurationProperties(StorageProperties.class)
public class LocalStorageService implements StorageService {

    private final StorageProperties properties;

    public LocalStorageService(StorageProperties properties) {
        this.properties = properties;
    }

    @Override
    public String store(String folder, MultipartFile file) {
        validateImage(file);

        String extension = extensionFor(file);
        String key = UUID.randomUUID() + extension;
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

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw StorageException.badRequest("File is required");
        }
        if (file.getSize() > properties.getMaxFileSize()) {
            throw new StorageException(HttpStatus.PAYLOAD_TOO_LARGE,
                    "File exceeds the maximum size of " + properties.getMaxFileSize() + " bytes");
        }
        String contentType = file.getContentType();
        if (contentType == null || !properties.getAllowedImageTypes().contains(contentType)) {
            throw StorageException.badRequest("Unsupported file type: " + contentType);
        }
    }

    private String extensionFor(MultipartFile file) {
        String original = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String ext = StringUtils.getFilenameExtension(original);
        return ext == null || ext.isBlank() ? "" : "." + ext.toLowerCase();
    }

    private String joinUrl(String base, String folder, String key) {
        String trimmedBase = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        return trimmedBase + "/" + folder + "/" + key;
    }
}
