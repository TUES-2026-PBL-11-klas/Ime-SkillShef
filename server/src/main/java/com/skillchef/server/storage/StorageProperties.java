package com.skillchef.server.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/** Configuration for the local-filesystem storage implementation. */
@ConfigurationProperties(prefix = "skillchef.storage")
public class StorageProperties {

    /** Directory on disk where uploaded files are written. */
    private String localDir = "uploads";

    /** Public base URL that maps to {@link #localDir} (used to build returned URLs). */
    private String publicBaseUrl = "http://localhost:8080/uploads";

    /** Maximum accepted file size in bytes. */
    private long maxFileSize = 5 * 1024 * 1024;

    /** Allowed MIME types for image uploads (avatars, thumbnails, etc.). */
    private List<String> allowedImageTypes = List.of("image/png", "image/jpeg", "image/webp", "image/gif");

    public String getLocalDir() {
        return localDir;
    }

    public void setLocalDir(String localDir) {
        this.localDir = localDir;
    }

    public String getPublicBaseUrl() {
        return publicBaseUrl;
    }

    public void setPublicBaseUrl(String publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public List<String> getAllowedImageTypes() {
        return allowedImageTypes;
    }

    public void setAllowedImageTypes(List<String> allowedImageTypes) {
        this.allowedImageTypes = allowedImageTypes;
    }
}
