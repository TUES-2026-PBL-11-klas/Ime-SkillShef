package com.skillchef.server.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Strongly-typed binding for {@code skillchef.storage.*} configuration. */
@ConfigurationProperties(prefix = "skillchef.storage")
public class StorageProperties {

    /** S3-compatible endpoint URL. Leave blank for AWS S3; set to your R2 endpoint for Cloudflare R2. */
    private String endpoint = "";

    /** AWS/R2 region. Use {@code auto} for Cloudflare R2. */
    private String region = "auto";

    private String accessKeyId = "";
    private String secretAccessKey = "";
    private String bucket = "skillchef-media";

    /** Public CDN base URL (e.g. https://media.skillchef.com). Falls back to direct S3/R2 URL when blank. */
    private String cdnBaseUrl = "";

    /** Maximum upload size enforced before issuing a presigned URL, in megabytes. */
    private int maxFileSizeMb = 20;

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getAccessKeyId() { return accessKeyId; }
    public void setAccessKeyId(String accessKeyId) { this.accessKeyId = accessKeyId; }

    public String getSecretAccessKey() { return secretAccessKey; }
    public void setSecretAccessKey(String secretAccessKey) { this.secretAccessKey = secretAccessKey; }

    public String getBucket() { return bucket; }
    public void setBucket(String bucket) { this.bucket = bucket; }

    public String getCdnBaseUrl() { return cdnBaseUrl; }
    public void setCdnBaseUrl(String cdnBaseUrl) { this.cdnBaseUrl = cdnBaseUrl; }

    public int getMaxFileSizeMb() { return maxFileSizeMb; }
    public void setMaxFileSizeMb(int maxFileSizeMb) { this.maxFileSizeMb = maxFileSizeMb; }
}
