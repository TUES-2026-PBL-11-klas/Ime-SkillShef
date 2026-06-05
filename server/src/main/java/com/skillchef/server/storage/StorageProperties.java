package com.skillchef.server.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

/** Configuration shared by every {@link StorageService} implementation. */
@ConfigurationProperties(prefix = "skillchef.storage")
public class StorageProperties {

    /** Active backend: {@code local} (filesystem) or {@code s3} (S3 / Cloudflare R2). */
    private String provider = "s3";

    /** Directory on disk where uploaded files are written (local provider only). */
    private String localDir = "uploads";

    /** Public base URL that maps to {@link #localDir} (local provider only). */
    private String publicBaseUrl = "http://localhost:8080/uploads";

    /** Maximum accepted file size in bytes. */
    private long maxFileSize = 5 * 1024 * 1024;

    /** Allowed MIME types for image uploads (avatars, thumbnails, etc.). */
    private List<String> allowedImageTypes = List.of("image/png", "image/jpeg", "image/webp", "image/gif");

    /** Settings for the S3 / Cloudflare R2 backend (used when {@link #provider} is {@code s3}). */
    @NestedConfigurationProperty
    private S3 s3 = new S3();

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

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

    public S3 getS3() {
        return s3;
    }

    public void setS3(S3 s3) {
        this.s3 = s3;
    }

    /** S3 / Cloudflare R2 connection and addressing settings. */
    public static class S3 {

        /** Target bucket that uploaded objects are written to. */
        private String bucket;

        /**
         * Region for the bucket. AWS uses values like {@code eu-central-1};
         * Cloudflare R2 uses {@code auto}.
         */
        private String region = "auto";

        /**
         * Custom endpoint for S3-compatible backends (e.g. R2:
         * {@code https://<account-id>.r2.cloudflarestorage.com}). Leave blank for AWS S3.
         */
        private String endpoint = "";

        /** Access key id. Blank falls back to the default AWS credentials chain. */
        private String accessKeyId = "";

        /** Secret access key. Blank falls back to the default AWS credentials chain. */
        private String secretAccessKey = "";

        /**
         * Public base URL of the CDN / bucket that uploaded objects are reachable at,
         * used to build the returned URLs (e.g. {@code https://cdn.skillchef.com}).
         */
        private String publicBaseUrl = "";

        /**
         * Use path-style addressing ({@code endpoint/bucket/key}) instead of
         * virtual-hosted style. Some S3-compatible backends require this.
         */
        private boolean pathStyleAccess = false;

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getSecretAccessKey() {
            return secretAccessKey;
        }

        public void setSecretAccessKey(String secretAccessKey) {
            this.secretAccessKey = secretAccessKey;
        }

        public String getPublicBaseUrl() {
            return publicBaseUrl;
        }

        public void setPublicBaseUrl(String publicBaseUrl) {
            this.publicBaseUrl = publicBaseUrl;
        }

        public boolean isPathStyleAccess() {
            return pathStyleAccess;
        }

        public void setPathStyleAccess(boolean pathStyleAccess) {
            this.pathStyleAccess = pathStyleAccess;
        }
    }
}
