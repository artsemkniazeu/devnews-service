package pl.dev.news.devnewsservice.config;

import com.twilio.Twilio;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final AppConfiguration appConfiguration;

    @Bean
    public Yaml yaml() {
        final LoaderOptions options = new LoaderOptions();
        options.setAllowDuplicateKeys(false);
        return new Yaml(options);
    }

    /**
     * Bean wrote to have base HTTP request and multipart uploading.
     * We are replacing standard {@link org.springframework.web.multipart.support.StandardServletMultipartResolver}
     * to CommonsMultipartResolver to achieve this.
     */
    @Bean(name = "commonsMultipartResolver")
    public CommonsMultipartResolver commonsMultipartResolver() {
        final CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setResolveLazily(true);
        return multipartResolver;
    }

    @PostConstruct
    public void twilioInit() {
        Twilio.init(
                appConfiguration.getTwilio().getAccountSid(),
                appConfiguration.getTwilio().getAuthToken()
        );
    }

}
