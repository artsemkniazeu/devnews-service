package pl.dev.news.devnewsservice.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import io.jsonwebtoken.io.Decoders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GoogleStorageConfiguration {

    private final AppConfiguration appConfiguration;

    @Bean
    @Profile("!test")
    public Storage storage() {
        final ServiceAccountCredentials credentials = credentials();
        return StorageOptions.newBuilder()
                .setCredentials(credentials())
                .setProjectId(credentials.getProjectId())
                .build().getService();
    }

    public ServiceAccountCredentials credentials() {
        ServiceAccountCredentials credentials = null;
        final String key = appConfiguration.getGoogle().getStorage().getKey();
        try (InputStream inputStream = new ByteArrayInputStream(
                Decoders.BASE64.decode(key.replace(" ", ""))
        )) {
            credentials = (ServiceAccountCredentials) GoogleCredentials.fromStream(inputStream);
        } catch (final IOException e) {
            log.error(e.getMessage());
        }
        return credentials;
    }

}
