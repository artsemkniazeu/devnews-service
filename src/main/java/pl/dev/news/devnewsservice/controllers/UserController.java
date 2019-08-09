package pl.dev.news.devnewsservice.controllers;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pl.dev.news.controllers.api.UserApi;
import pl.dev.news.devnewsservice.service.UserService;
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
    public ResponseEntity<RestUserModel> getUser(final UUID userId) {
        final RestUserModel model = userService.get(userId);
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<RestUserModel>> getUsers(
            @Valid final String name,
            @Valid final String username,
            @Valid final String email,
            @Min(1) @Valid final Integer page,
            @Min(10) @Max(30) @Valid final Integer size) {
        return null;
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
