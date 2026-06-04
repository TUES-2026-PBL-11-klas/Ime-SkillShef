package com.skillchef.server.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * Unified upload abstraction shared across feature modules (avatars, lesson
 * thumbnails, submission media, recipe images).
 *
 * <p>This is the seam for Person 4's storage layer (S3 / Cloudflare R2 + CDN).
 * The current {@link LocalStorageService} implementation writes to the local
 * filesystem so the rest of the app can be built and tested; swapping in the
 * cloud-backed implementation requires no changes to callers.
 */
public interface StorageService {

    /**
     * Stores an uploaded file under the given logical folder and returns a
     * publicly reachable URL for it.
     *
     * @param folder logical bucket/prefix (e.g. {@code "avatars"})
     * @param file   the uploaded multipart file
     * @return the public URL of the stored object
     */
    String store(String folder, MultipartFile file);
}
