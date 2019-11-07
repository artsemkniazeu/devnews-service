package pl.dev.news.devnewsservice.service.impl;

import com.google.common.collect.ImmutableMap;
import com.samskivert.mustache.Mustache;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mustache.MustacheResourceTemplateLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pl.dev.news.devnewsservice.config.AppConfiguration;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.service.MailService;

import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private static final String EMAIL_ACTIVATION_TEMPLATE = "activation-email.html";
    private static final String EMAIL_WELCOME_TEMPLATE = "welcome-email.html";

    private final AppConfiguration appConfiguration;

    private final JavaMailSender javaMailSender;

    private final Mustache.Compiler compiler;

    private final MustacheResourceTemplateLoader templateLoader;

    @Override
    public void sendEmailActivationCode(final UserEntity entity) {
        final String buttonUrl = fromHttpUrl(appConfiguration.getUrls().getConfirmUrl())
                .queryParam("key", entity.getActivationKey())
                .toUriString();
        final Map<String, Object> context = createContext(entity, buttonUrl);
        final String content = compileTemplate(EMAIL_ACTIVATION_TEMPLATE, context);
        sendEmail(entity.getEmail(), "Email verification on DevNews", content);
    }

    @Override
    public void sendWelcomeEmail(final UserEntity entity) {
        final Map<String, Object> context = createContext(entity);
        final String content = compileTemplate(EMAIL_WELCOME_TEMPLATE, context);
        sendEmail(entity.getEmail(), "Email verification on DevNews", content);
    }

    @Override
    public void sendChangeEmailActivationCode(final UserEntity entity, final String key) {
        final String buttonUrl = fromHttpUrl(appConfiguration.getUrls().getEmailConfirmUrl())
                .queryParam("key", key)
                .toUriString();
        final Map<String, Object> context = createContext(entity, buttonUrl);
        final String content = compileTemplate(EMAIL_ACTIVATION_TEMPLATE, context);
        sendEmail(entity.getEmail(), "Email verification on DevNews", content);
    }

    private Map<String, Object> createContext(final UserEntity entity) {
        return createContext(entity, "");
    }

    @Override
    public void sendWelcomeEmail(final UserEntity entity) {
        final Map<String, Object> context = createContext(entity);
        final String content = compileTemplate(EMAIL_WELCOME_TEMPLATE, context);
        sendEmail(entity.getEmail(), "Email verification on DevNews", content);
    }

    private Map<String, Object> createContext(final UserEntity entity) {
        return createContext(entity,"");
    }

    private Map<String, Object> createContext(final UserEntity entity, final String buttonUrl) {
        return new ImmutableMap.Builder<String, Object>()
                .put("webUrl", appConfiguration.getUrls().getWebUrl())
                .put("buttonUrl", buttonUrl)
                .put("email", entity.getEmail())
                .put("firstName", entity.getFirstName())
                .put("lastName", entity.getLastName())
                .build();
    }

    @SneakyThrows
    private String compileTemplate(final String templateName, final Object context) {
        return compiler.compile(
                templateLoader.getTemplate(templateName)
        ).execute(context);
    }

    @SneakyThrows
    private void sendEmail(final String to, final String subject, final String content) {
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, StandardCharsets.UTF_8.name());
        message.setFrom(appConfiguration.getMail().getFrom());
        message.setReplyTo(appConfiguration.getMail().getReplyTo());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content, true);
        javaMailSender.send(mimeMessage);
    }
}
