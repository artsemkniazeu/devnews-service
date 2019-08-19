package pl.dev.news.devnewsservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.dev.news.controller.api.AuthApi;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.security.TokenProvider;
import pl.dev.news.devnewsservice.service.AuthService;
import pl.dev.news.devnewsservice.utils.HeaderUtils;
import pl.dev.news.model.rest.RestRefreshTokenRequest;
import pl.dev.news.model.rest.RestSignInRequest;
import pl.dev.news.model.rest.RestSignUpRequest;
import pl.dev.news.model.rest.RestTokenResponse;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;
import static pl.dev.news.controller.api.UserApi.getUserPath;

@RestController
@AllArgsConstructor
public class AuthApiController implements AuthApi {

    private final AuthService authService;

    private final TokenProvider tokenProvider;

    @Override
    public ResponseEntity<RestTokenResponse> signIn(
            @Valid @RequestBody final RestSignInRequest restSignInRequest
    ) {
        final RestTokenResponse tokenResponse = authService.signIn(restSignInRequest);
        return new ResponseEntity<>(tokenResponse, generateHeaders(tokenResponse), CREATED);
    }

    @Override
    public ResponseEntity<RestTokenResponse> signUp(
            @Valid @RequestBody final RestSignUpRequest restSignupRequest
    ) {
        final RestTokenResponse tokenResponse = authService.signUp(restSignupRequest);
        return new ResponseEntity<>(tokenResponse, generateHeaders(tokenResponse), CREATED);
    }

    @Override
    public ResponseEntity<RestTokenResponse> refreshToken(
            @Valid @RequestBody final RestRefreshTokenRequest restRefreshTokenRequest
    ) {
        final RestTokenResponse tokenResponse = authService.refreshToken(restRefreshTokenRequest);
        return new ResponseEntity<>(tokenResponse, CREATED);
    }

    private HttpHeaders generateHeaders(final RestTokenResponse tokenResponse) {
        final UserEntity userEntity = tokenProvider.buildUserEntityByToken(tokenResponse.getAccess().getToken());
        assert userEntity != null;
        return HeaderUtils.generateLocationHeader(getUserPath, userEntity.getId());
    }

}
