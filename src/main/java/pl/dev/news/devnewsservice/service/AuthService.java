package pl.dev.news.devnewsservice.service;

import pl.dev.news.model.rest.RestRefreshTokenRequest;
import pl.dev.news.model.rest.RestSignInRequest;
import pl.dev.news.model.rest.RestSignUpRequest;
import pl.dev.news.model.rest.RestTokenResponse;

import java.util.UUID;

public interface AuthService {

    RestTokenResponse signIn(RestSignInRequest restSignInRequest);

    RestTokenResponse refreshToken(RestRefreshTokenRequest restRefreshTokenRequest);

    void signUp(RestSignUpRequest restSignupRequest);

    void activate(UUID key);

    void emailActivate(String key);
}
