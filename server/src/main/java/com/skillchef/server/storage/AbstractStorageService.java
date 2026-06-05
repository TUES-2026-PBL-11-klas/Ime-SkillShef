package com.skillchef.server.storage;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Shared upload concerns for every {@link StorageService} implementation:
 * file type / size validation, object key generation, and URL assembly.
 * Concrete backends ({@link LocalStorageService}, {@link S3StorageService})
 * only implement the persistence step.
 */
abstract class AbstractStorageService implements StorageService {

    protected final StorageProperties properties;

    protected AbstractStorageService(StorageProperties properties) {
        this.properties = properties;
    }

    /** Validates the upload against the configured size and MIME-type limits. */
    protected void validateImage(MultipartFile file) {
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

    /** Builds a collision-free object key ({@code <uuid>.<ext>}) for the upload. */
    protected String generateKey(MultipartFile file) {
        return UUID.randomUUID() + extensionFor(file);
    }

    private String extensionFor(MultipartFile file) {
        String original = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String ext = StringUtils.getFilenameExtension(original);
        return ext == null || ext.isBlank() ? "" : "." + ext.toLowerCase();
    }

    /** Joins a public base URL, logical folder, and object key into a single URL. */
    protected String joinUrl(String base, String folder, String key) {
        String trimmedBase = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        return trimmedBase + "/" + folder + "/" + key;
    }
}
