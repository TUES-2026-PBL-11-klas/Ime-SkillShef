package com.skillchef.server.storage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Request/response payloads for the storage endpoints. */
public final class StorageDtos {

    private StorageDtos() {}

    /**
     * Ask for a presigned PUT URL before uploading a file.
     * The client uploads directly to S3/R2 using the returned {@code uploadUrl}.
     */
    public record PresignRequest(
            @NotBlank String filename,
            @NotBlank String contentType,
            @NotNull UploadType uploadType
    ) {}

    /** Returned by the presign endpoint. */
    public record PresignResponse(
            /** Presigned PUT URL — upload directly to this URL with the matching Content-Type header. */
            String uploadUrl,
            /** Object key — store this and pass it to /process after the upload completes. */
            String key,
            /** Final CDN URL the object will be accessible at after upload. */
            String cdnUrl,
            int expiresInSeconds
    ) {}

    /** Call this after a successful direct upload to trigger thumbnail generation. */
    public record ProcessRequest(
            @NotBlank String key,
            @NotNull UploadType uploadType
    ) {}

    /** Returned by the process endpoint. */
    public record ProcessResponse(
            String originalUrl,
            /** Null when the upload type does not support image resizing (e.g. videos). */
            String thumbnailUrl
    ) {}
}
