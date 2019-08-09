package pl.dev.news.devnewsservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "app.jwt")
public class AppConfiguration {

    private String base64Secret;
    private long accessTokenValidity;
    private long refreshTokenValidity;
}
