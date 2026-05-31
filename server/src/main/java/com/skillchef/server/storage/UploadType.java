package com.skillchef.server.storage;

import java.util.Set;

/** Classifies an upload and carries the rules that apply to it. */
public enum UploadType {

    AVATAR {
        @Override public String keyPrefix() { return "avatars"; }
        @Override public Set<String> allowedContentTypes() {
            return Set.of("image/jpeg", "image/png", "image/webp");
        }
        @Override public boolean supportsImageResize() { return true; }
        @Override public int thumbnailWidth()  { return 200; }
        @Override public int thumbnailHeight() { return 200; }
        @Override public boolean squareCrop()  { return true; }
    },

    RECIPE_IMAGE {
        @Override public String keyPrefix() { return "recipes"; }
        @Override public Set<String> allowedContentTypes() {
            return Set.of("image/jpeg", "image/png", "image/webp");
        }
        @Override public boolean supportsImageResize() { return true; }
        @Override public int thumbnailWidth()  { return 400; }
        @Override public int thumbnailHeight() { return 300; }
        @Override public boolean squareCrop()  { return false; }
    },

    LESSON_THUMBNAIL {
        @Override public String keyPrefix() { return "lessons"; }
        @Override public Set<String> allowedContentTypes() {
            return Set.of("image/jpeg", "image/png", "image/webp");
        }
        @Override public boolean supportsImageResize() { return true; }
        @Override public int thumbnailWidth()  { return 640; }
        @Override public int thumbnailHeight() { return 360; }
        @Override public boolean squareCrop()  { return false; }
    },

    SUBMISSION_MEDIA {
        @Override public String keyPrefix() { return "submissions"; }
        @Override public Set<String> allowedContentTypes() {
            return Set.of("image/jpeg", "image/png", "image/webp", "video/mp4", "video/quicktime");
        }
        @Override public boolean supportsImageResize() { return false; }
        @Override public int thumbnailWidth()  { return 0; }
        @Override public int thumbnailHeight() { return 0; }
        @Override public boolean squareCrop()  { return false; }
    };

    public abstract String keyPrefix();
    public abstract Set<String> allowedContentTypes();
    public abstract boolean supportsImageResize();
    public abstract int thumbnailWidth();
    public abstract int thumbnailHeight();
    /** When true, the thumbnail is cropped to an exact square rather than fitted. */
    public abstract boolean squareCrop();
}
