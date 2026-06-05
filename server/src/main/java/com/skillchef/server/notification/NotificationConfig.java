package com.skillchef.server.notification;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Enables async execution for non-blocking email dispatch and registers
 * the notification configuration properties.
 */
@Configuration
@EnableAsync
@EnableConfigurationProperties(NotificationProperties.class)
public class NotificationConfig {
}
