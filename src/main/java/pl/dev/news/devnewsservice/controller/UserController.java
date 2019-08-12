package pl.dev.news.devnewsservice.controller;


import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pl.dev.news.controller.api.UserApi;
import pl.dev.news.devnewsservice.service.UserService;
import pl.dev.news.devnewsservice.utils.HeaderUtil;
import pl.dev.news.model.rest.RestUrlModel;
import pl.dev.news.model.rest.RestUserModel;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    @PreAuthorize("@securityResolver.isResourceOwner(#userId) || hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") final UUID userId) {
        userService.delete(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<RestUserModel> getUser(@PathVariable("userId") final UUID userId) {
        final RestUserModel model = userService.get(userId);
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<RestUserModel>> getUsers(
            @Valid @RequestParam(value = "name", required = false, defaultValue = "")  final String name,
            @Valid @RequestParam(value = "username", required = false, defaultValue = "") final String username,
            @Valid @RequestParam(value = "email", required = false, defaultValue = "") final String email,
            @Min(1) @Valid
            @RequestParam(value = "page", required = false, defaultValue = "1") final Integer page,
            @Min(10) @Max(30) @Valid
            @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size
    ) {
        final Page<RestUserModel> users = userService.getUsers(username, name, email, page, size);
        final HttpHeaders headers = HeaderUtil.generatePaginationHeaders(basePath, users);
        return new ResponseEntity<>(users.getContent(), headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RestUserModel> updateUser(
            final UUID userId,
            @Valid final RestUserModel restUserModel
    ) {
        return null;
    }

    @Override
    public ResponseEntity<RestUrlModel> uploadImage(
            final UUID userId,
            @Valid final MultipartFile file
    ) {
        return null;
    }
}
