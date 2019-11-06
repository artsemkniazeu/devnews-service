package pl.dev.news.devnewsservice.service;

import org.springframework.scheduling.annotation.Async;
import pl.dev.news.devnewsservice.entity.UserEntity;

public interface MailService {

    @Async
    void sendEmailActivationCode(UserEntity entity);

    @Async
    void sendWelcomeEmail(UserEntity entity);

    @Async
    void sendChangeEmailActivationCode(UserEntity entity, String key);
}
