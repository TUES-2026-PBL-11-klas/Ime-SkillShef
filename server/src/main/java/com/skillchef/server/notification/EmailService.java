package com.skillchef.server.notification;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.skillchef.server.user.User;
import com.skillchef.server.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

/**
 * Sends transactional notification emails via SendGrid.
 *
 * Email dispatch is {@code @Async} — it never blocks the HTTP response and
 * never causes a like/comment/follow operation to fail.
 *
 * Sending is skipped entirely when {@code SENDGRID_API_KEY} is not configured.
 *
 * TODO (Person 1): read {@code Profile.preferences.emailNotifications} to honour
 *   the user's email opt-in setting before calling this service.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final NotificationProperties props;
    private final UserRepository userRepository;

    public EmailService(NotificationProperties props, UserRepository userRepository) {
        this.props = props;
        this.userRepository = userRepository;
    }

    @Async
    public void sendNotificationEmailAsync(UUID recipientId, NotificationType type) {
        if (!props.isEmailEnabled()) return;

        User recipient = userRepository.findById(recipientId).orElse(null);
        if (recipient == null) return;

        String subject = subject(type);
        String body    = body(type);

        try {
            SendGrid client = new SendGrid(props.getSendgridApiKey());
            Mail mail = new Mail(
                    new Email(props.getFromEmail(), props.getFromName()),
                    subject,
                    new Email(recipient.getEmail()),
                    new Content("text/plain", body)
            );

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = client.api(request);
            if (response.getStatusCode() >= 400) {
                log.warn("SendGrid returned {} sending {} email to {}",
                        response.getStatusCode(), type, recipientId);
            }
        } catch (IOException e) {
            log.error("Failed to send {} notification email to {}: {}", type, recipientId, e.getMessage());
        }
    }

    private static String subject(NotificationType type) {
        return switch (type) {
            case LIKE    -> "Someone liked your recipe!";
            case COMMENT -> "Someone commented on your recipe!";
            case FOLLOW  -> "You have a new follower!";
        };
    }

    private static String body(NotificationType type) {
        return switch (type) {
            case LIKE    -> "Someone liked one of your recipes on SkillChef. Check it out!";
            case COMMENT -> "Someone left a comment on your recipe on SkillChef. Check it out!";
            case FOLLOW  -> "You have a new follower on SkillChef!";
        };
    }
}
