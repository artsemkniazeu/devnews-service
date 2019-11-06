package pl.dev.news.devnewsservice.controller;


import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pl.dev.news.controller.api.UserApi;
import pl.dev.news.devnewsservice.service.UserService;
import pl.dev.news.devnewsservice.utils.HeaderUtils;
import pl.dev.news.model.rest.RestEmailModel;
import pl.dev.news.model.rest.RestPhoneModel;
import pl.dev.news.model.rest.RestPhoneResponseModel;
import pl.dev.news.model.rest.RestUploadModel;
import pl.dev.news.model.rest.RestUserModel;
import pl.dev.news.model.rest.RestUserQueryParameters;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@AllArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    @PreAuthorize("@securityResolver.isResourceOwner(#userId) || hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") final UUID userId) {
        userService.delete(userId);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @Override
    public ResponseEntity<RestUserModel> getUser(@PathVariable("userId") final UUID userId) {
        final RestUserModel model = userService.get(userId);
        final HttpHeaders headers = HeaderUtils.generateLocationHeader(getUserPath, userId);
        return new ResponseEntity<>(model, headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<RestUserModel>> getUsers(
            @Valid final RestUserQueryParameters parameters,
            @Min(1) @Valid
            @RequestParam(value = "page", required = false, defaultValue = "1") final Integer page,
            @Min(10) @Max(30) @Valid
            @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size
    ) {
        final Page<RestUserModel> users = userService.getUsers(parameters, page, size);
        final HttpHeaders headers = HeaderUtils.generatePaginationHeaders(basePath, users);
        return new ResponseEntity<>(users.getContent(), headers, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<RestUserModel> updateUser(
            @PathVariable("userId") final UUID userId,
            @Valid @RequestBody final RestUserModel restUserModel
    ) {
        final RestUserModel user = userService.update(userId, restUserModel);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RestUploadModel> uploadImage(
            @PathVariable("userId") final UUID userId,
            @Valid @RequestPart("file") final MultipartFile file) {
        final RestUploadModel restUploadModel = userService.uploadImage(userId, file);
        return new ResponseEntity<>(restUploadModel, HttpStatus.CREATED);
    }


    @Override
    public ResponseEntity<RestUploadModel> uploadBackground(
            @PathVariable("userId") final UUID userId,
            @Valid @RequestPart("file") final MultipartFile file
    ) {
        final RestUploadModel restUploadModel = userService.uploadBackground(userId, file);
        return new ResponseEntity<>(restUploadModel, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<RestPhoneResponseModel> phoneSendVerificationCode(
            @PathVariable("userId") final UUID userId,
            @Valid @RequestBody final RestPhoneModel restPhoneModel
    ) {
        final RestPhoneResponseModel restPhoneResponseModel = userService.sendVerificationCode(userId, restPhoneModel);
        return new ResponseEntity<>(restPhoneResponseModel, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<RestPhoneResponseModel> phoneVerifyAndUpdate(
            @PathVariable("userId") final UUID userId,
            @Valid @RequestBody final RestPhoneModel restPhoneModel
    ) {
        final RestPhoneResponseModel restPhoneResponseModel = userService.verifyPhoneNumber(userId, restPhoneModel);
        return new ResponseEntity<>(restPhoneResponseModel, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<Void> resendActivationCode(@PathVariable("email") final String email) {
        userService.resendActivationCode(email);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @Override
    public ResponseEntity<Void> changeEmailAddress(
            @PathVariable("userId") final UUID userId,
            @Valid @RequestBody final RestEmailModel restEmailModel
    ) {
        userService.changeEmailAddress(userId, restEmailModel);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @Override
    public ResponseEntity<Void> unfollowUser(final UUID userId) {
        userService.unFollow(userId);
        return new ResponseEntity<>(OK);
    }

    @Override
    public ResponseEntity<Void> followUser(final UUID userId) {
        userService.follow(userId);
        return new ResponseEntity<>(OK);
    }

}
