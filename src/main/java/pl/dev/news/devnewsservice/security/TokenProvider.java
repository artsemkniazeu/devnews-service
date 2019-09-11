package pl.dev.news.devnewsservice.security;

import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.model.rest.RestTokenResponse;

public interface TokenProvider {

    RestTokenResponse createTokenModel(UserEntity userEntity);

    RestTokenResponse refreshToken(String refreshToken);

    UserEntity buildUserEntityByToken(String token);
}
