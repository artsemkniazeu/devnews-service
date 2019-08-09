package pl.dev.news.devnewsservice.service;

import pl.dev.news.model.rest.RestLoginRequest;
import pl.dev.news.model.rest.RestRefreshTokenRequest;
import pl.dev.news.model.rest.RestSignUpRequest;
import pl.dev.news.model.rest.RestTokenModel;
import pl.dev.news.model.rest.RestUserModel;

public interface AuthService {

    RestTokenModel login(RestLoginRequest restLoginRequest);

    RestTokenModel refreshToken(RestRefreshTokenRequest restRefreshTokenRequest);

    RestUserModel signUp(RestSignUpRequest restSignupRequest);

}
