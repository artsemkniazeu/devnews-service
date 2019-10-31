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

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.springframework.web.util.UriComponentsBuilder.fromPath;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private static final String EMAIL_ACTIVATION_TEMPLATE = "activation-email.html";

    private final AppConfiguration appConfiguration;

    private final JavaMailSender javaMailSender;

    private final Mustache.Compiler compiler;

    private final MustacheResourceTemplateLoader templateLoader;

    @Override
    public void sendActivationEmail(final UserEntity entity) {
        final String buttonUrl = fromPath(appConfiguration.getUrls().getConfirmUrl())
                .queryParam("key", entity.getActivationKey())
                .toUriString();

        final Map<String, Object> context = createContext(entity, buttonUrl);
        final String content = compileTemplate(EMAIL_ACTIVATION_TEMPLATE ,context);
        sendEmail(entity.getEmail(), "Email verification on DevNews", content);

    }

    private Map<String, Object> createContext(final UserEntity entity, final String buttonUrl) {
        return new ImmutableMap.Builder<String, Object>()
                .put("webUrl", appConfiguration.getUrls().getWebUrl())
                .put("buttonUrl", buttonUrl)
                .put("email", entity.getEmail())
                .put("firstname", entity.getFirstName())
                .put("lastname", entity.getLastName())
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
        var mimeMessage = javaMailSender.createMimeMessage();
        var message = new MimeMessageHelper(mimeMessage, false, StandardCharsets.UTF_8.name());
        message.setFrom(appConfiguration.getMail().getFrom());
        message.setReplyTo(appConfiguration.getMail().getReplyTo());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content, true);
        javaMailSender.send(mimeMessage);
    }
}