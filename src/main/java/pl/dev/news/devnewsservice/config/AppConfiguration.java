package pl.dev.news.devnewsservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "app")
public class AppConfiguration {

    private final Jwt jwt;
    private final Google google;
    private final Twilio twilio;
    private final Urls urls;
    private final Mail mail;

    public AppConfiguration() {
        this.jwt = new Jwt();
        this.google = new Google();
        this.twilio = new Twilio();
        this.urls = new Urls();
        this.mail = new Mail();
    }

    @Getter
    @Setter
    public class Jwt {
        private String base64Secret;
        private long accessTokenValidity;
        private long refreshTokenValidity;
    }

    @Getter
    @Setter
    public class Google {

        private Storage storage = new Storage();

        @Getter
        @Setter
        public class Storage {
            private String imageBucket;
            private String videoBucket;
            private String key;
        }
    }

    @Getter
    @Setter
    public class Twilio {
        private String accountSid;
        private String authToken;
        private String serviceSid;
    }

    @Getter
    @Setter
    public class Urls {
        private String webUrl;
        private String confirmUrl;
        private String emailConfirmUrl;
        private String resetUrl;
    }

    @Getter
    @Setter
    public class Mail {

        @Email
        @NotBlank
        private String from;

        @Email
        @NotBlank
        private String replyTo;

    }

}
