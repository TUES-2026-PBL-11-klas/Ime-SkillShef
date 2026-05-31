package com.skillchef.server.storage;

import com.skillchef.server.auth.jwt.AuthPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.skillchef.server.storage.StorageDtos.*;

/**
 * Storage endpoints consumed by all feature modules.
 *
 * All routes require a valid JWT (enforced by SecurityConfig).
 *
 * Typical client flow:
 *   POST /api/storage/presign  → receive uploadUrl + key
 *   PUT  {uploadUrl}           → upload file directly (client → S3/R2)
 *   POST /api/storage/process  → receive final CDN URLs (originalUrl + thumbnailUrl)
 *
 * The key is scoped to the authenticated user ({prefix}/{userId}/{uuid}.ext),
 * so ownership is enforced on the process and delete endpoints.
 */
@RestController
@RequestMapping("/api/storage")
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    /** Step 1: obtain a presigned PUT URL for a direct client-to-storage upload. */
    @PostMapping("/presign")
    public PresignResponse presign(
            @Valid @RequestBody PresignRequest req,
            @AuthenticationPrincipal AuthPrincipal principal) {
        return storageService.presignUpload(req, principal.userId());
    }

    /** Step 2 (post-upload): generate a thumbnail and return all CDN URLs. */
    @PostMapping("/process")
    public ProcessResponse process(
            @Valid @RequestBody ProcessRequest req,
            @AuthenticationPrincipal AuthPrincipal principal) {
        assertOwnership(req.key(), req.uploadType(), principal);
        return storageService.processUpload(req.key(), req.uploadType());
    }

    /** Delete an object (and its thumbnail if one was generated). */
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestParam String key,
            @RequestParam UploadType uploadType,
            @AuthenticationPrincipal AuthPrincipal principal) {
        assertOwnership(key, uploadType, principal);
        storageService.deleteObject(key);
        // Best-effort thumbnail cleanup — no error if thumbnail doesn't exist.
        String thumbKey = key.replaceAll("\\.[^.]+$", "_thumb.jpg");
        if (!thumbKey.equals(key)) {
            try { storageService.deleteObject(thumbKey); } catch (Exception ignored) {}
        }
    }

    private static void assertOwnership(String key, UploadType uploadType, AuthPrincipal principal) {
        String expectedPrefix = uploadType.keyPrefix() + "/" + principal.userId() + "/";
        if (!key.startsWith(expectedPrefix)) {
            throw StorageException.forbidden("You do not own this resource");
        }
    }
}
