package com.skillchef.server.storage;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

import static com.skillchef.server.storage.StorageDtos.*;

/**
 * Shared storage SDK consumed by all feature modules (avatars, recipe images,
 * lesson thumbnails, submission media). Backed by any S3-compatible store
 * (AWS S3 or Cloudflare R2).
 *
 * Upload flow:
 *   1. POST /api/storage/presign  → get a presigned PUT URL + the final CDN key
 *   2. PUT {uploadUrl}            → upload directly from the client to S3/R2
 *   3. POST /api/storage/process  → generate thumbnail and confirm the CDN URLs
 */
@Service
public class StorageService {

    private static final Logger log = LoggerFactory.getLogger(StorageService.class);
    private static final int PRESIGN_TTL_MINUTES = 5;
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final StorageProperties props;

    public StorageService(S3Client s3Client, S3Presigner s3Presigner, StorageProperties props) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.props = props;
    }

    /**
     * Validates the upload request and returns a presigned PUT URL the client can use
     * to upload directly to S3/R2. No bytes pass through the backend.
     */
    public PresignResponse presignUpload(PresignRequest req, UUID userId) {
        assertConfigured();

        if (!req.uploadType().allowedContentTypes().contains(req.contentType())) {
            throw StorageException.badRequest(
                    "Content-Type '" + req.contentType() + "' is not allowed for " + req.uploadType());
        }

        String ext = extractExtension(req.filename());
        String key = req.uploadType().keyPrefix()
                + "/" + userId
                + "/" + UUID.randomUUID()
                + "." + ext;

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(r -> r
                .signatureDuration(Duration.ofMinutes(PRESIGN_TTL_MINUTES))
                .putObjectRequest(p -> p
                        .bucket(props.getBucket())
                        .key(key)
                        .contentType(req.contentType())
                        .contentLength((long) props.getMaxFileSizeMb() * 1024 * 1024)
                )
        );

        return new PresignResponse(
                presigned.url().toString(),
                key,
                buildCdnUrl(key),
                PRESIGN_TTL_MINUTES * 60
        );
    }

    /**
     * Called after the client has finished uploading to S3/R2.
     * Downloads the original, generates a thumbnail for image uploads,
     * and returns the CDN URLs for both.
     */
    public ProcessResponse processUpload(String key, UploadType uploadType) {
        assertConfigured();

        String originalUrl = buildCdnUrl(key);

        if (!uploadType.supportsImageResize() || !isImageKey(key)) {
            return new ProcessResponse(originalUrl, null);
        }

        try {
            byte[] original = s3Client.getObjectAsBytes(r -> r
                    .bucket(props.getBucket())
                    .key(key)
            ).asByteArray();

            byte[] thumbnail = resizeImage(original, uploadType);

            String thumbKey = replaceExtension(key, "_thumb.jpg");
            s3Client.putObject(
                    r -> r.bucket(props.getBucket())
                          .key(thumbKey)
                          .contentType("image/jpeg")
                          .contentLength((long) thumbnail.length),
                    RequestBody.fromBytes(thumbnail)
            );

            log.debug("Generated thumbnail {} ({} bytes)", thumbKey, thumbnail.length);
            return new ProcessResponse(originalUrl, buildCdnUrl(thumbKey));

        } catch (NoSuchKeyException e) {
            throw StorageException.notFound("Object not found: " + key);
        } catch (IOException e) {
            log.warn("Thumbnail generation failed for {}: {}", key, e.getMessage());
            return new ProcessResponse(originalUrl, null);
        }
    }

    /** Deletes a single object. Silently succeeds if the key does not exist. */
    public void deleteObject(String key) {
        assertConfigured();
        s3Client.deleteObject(r -> r.bucket(props.getBucket()).key(key));
    }

    // --- helpers ---

    private byte[] resizeImage(byte[] original, UploadType uploadType) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        var builder = Thumbnails.of(new ByteArrayInputStream(original))
                .size(uploadType.thumbnailWidth(), uploadType.thumbnailHeight())
                .outputFormat("jpg");

        if (uploadType.squareCrop()) {
            builder.crop(Positions.CENTER);
        } else {
            builder.keepAspectRatio(true);
        }

        builder.toOutputStream(baos);
        return baos.toByteArray();
    }

    private String buildCdnUrl(String key) {
        String base = props.getCdnBaseUrl();
        if (base == null || base.isBlank()) {
            return "https://" + props.getBucket()
                    + ".s3." + props.getRegion() + ".amazonaws.com/" + key;
        }
        return (base.endsWith("/") ? base.substring(0, base.length() - 1) : base) + "/" + key;
    }

    private void assertConfigured() {
        if (props.getAccessKeyId() == null || props.getAccessKeyId().isBlank()) {
            throw StorageException.internal(
                    "Storage is not configured — set STORAGE_ACCESS_KEY_ID and STORAGE_SECRET_ACCESS_KEY");
        }
    }

    private static String extractExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot + 1).toLowerCase() : "bin";
    }

    private static boolean isImageKey(String key) {
        return IMAGE_EXTENSIONS.contains(extractExtension(key));
    }

    private static String replaceExtension(String key, String suffix) {
        int dot = key.lastIndexOf('.');
        return dot >= 0 ? key.substring(0, dot) + suffix : key + suffix;
    }
}
