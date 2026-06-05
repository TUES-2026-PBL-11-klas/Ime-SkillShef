package com.skillchef.server.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

/**
 * Cloud {@link StorageService} backed by S3 or any S3-compatible store
 * (Cloudflare R2). Active when {@code skillchef.storage.provider=s3} — the
 * default outside of development. Objects are written under
 * {@code <folder>/<uuid>.<ext>} and the returned URL is built from the
 * configured CDN / public base URL. The {@link S3Client} is provided by
 * {@link S3StorageConfig}.
 */
@Service
@ConditionalOnProperty(name = "skillchef.storage.provider", havingValue = "s3", matchIfMissing = true)
public class S3StorageService extends AbstractStorageService {

    private final S3Client s3Client;

    public S3StorageService(StorageProperties properties, S3Client s3Client) {
        super(properties);
        this.s3Client = s3Client;
    }

    @Override
    public String store(String folder, MultipartFile file) {
        validateImage(file);

        StorageProperties.S3 s3 = properties.getS3();
        String key = generateKey(file);
        String objectKey = folder + "/" + key;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(s3.getBucket())
                .key(objectKey)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        try {
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException | S3Exception ex) {
            throw StorageException.internal("Failed to store uploaded file");
        }

        return joinUrl(s3.getPublicBaseUrl(), folder, key);
    }
}
