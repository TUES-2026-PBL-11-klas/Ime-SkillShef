package com.skillchef.server.notification;

/** Classifies what triggered an in-app notification. */
public enum NotificationType {
    /** Another user liked your recipe post. entity_id = recipe_post id. */
    LIKE,
    /** Another user commented on your recipe post. entity_id = recipe_post id. */
    COMMENT,
    /** Another user started following you. entity_id = null. */
    FOLLOW
}
