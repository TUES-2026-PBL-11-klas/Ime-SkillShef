package com.skillchef.server.notification;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Strongly-typed binding for {@code skillchef.notification.*} configuration. */
@ConfigurationProperties(prefix = "skillchef.notification")
public class NotificationProperties {

    /** SendGrid API key. Leave blank to disable email notifications. */
    private String sendgridApiKey = "";

    /** Sender email address shown in outbound emails. */
    private String fromEmail = "noreply@skillchef.com";

    /** Sender display name shown in outbound emails. */
    private String fromName = "SkillChef";

    public String getSendgridApiKey() { return sendgridApiKey; }
    public void setSendgridApiKey(String sendgridApiKey) { this.sendgridApiKey = sendgridApiKey; }

    public String getFromEmail() { return fromEmail; }
    public void setFromEmail(String fromEmail) { this.fromEmail = fromEmail; }

    public String getFromName() { return fromName; }
    public void setFromName(String fromName) { this.fromName = fromName; }

    public boolean isEmailEnabled() {
        return sendgridApiKey != null && !sendgridApiKey.isBlank();
    }
}
