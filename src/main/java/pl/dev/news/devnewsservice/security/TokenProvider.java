package pl.dev.news.devnewsservice.security;

import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.model.rest.RestTokenModel;

public interface TokenProvider {

    RestTokenModel createTokenModel(UserEntity userEntity);

    RestTokenModel refreshToken(String refreshToken);

    UserEntity buildUserEntityByToken(String token);
}
