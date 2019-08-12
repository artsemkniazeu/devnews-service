package pl.dev.news.devnewsservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.dev.news.controller.api.AuthApi;
import pl.dev.news.devnewsservice.service.AuthService;
import pl.dev.news.model.rest.RestLoginRequest;
import pl.dev.news.model.rest.RestRefreshTokenRequest;
import pl.dev.news.model.rest.RestSignUpRequest;
import pl.dev.news.model.rest.RestTokenModel;
import pl.dev.news.model.rest.RestUserModel;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;
import static pl.dev.news.controller.api.UserApi.getUserPath;
import static pl.dev.news.devnewsservice.utils.HeaderUtil.generateLocationHeader;

@RestController
@AllArgsConstructor
public class AuthApiController implements AuthApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<RestTokenModel> signIn(
            @Valid @RequestBody final RestLoginRequest restLoginRequest
    ) {
        final RestTokenModel tokenModel = authService.signIn(restLoginRequest);
        return new ResponseEntity<>(tokenModel, CREATED);
    }

    @Override
    public ResponseEntity<RestTokenModel> refreshToken(
            @Valid @RequestBody final RestRefreshTokenRequest restRefreshTokenRequest
    ) {
        final RestTokenModel tokenModel = authService.refreshToken(restRefreshTokenRequest);
        return new ResponseEntity<>(tokenModel, CREATED);
    }

    @Override
    public ResponseEntity<RestUserModel> signUp(
            @Valid @RequestBody final RestSignUpRequest restSignupRequest
    ) {
        final RestUserModel user = authService.signUp(restSignupRequest);
        final HttpHeaders headers = generateLocationHeader(getUserPath, user.getId());
        return new ResponseEntity<>(user, headers, CREATED);
    }

}
