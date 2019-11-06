package pl.dev.news.devnewsservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import pl.dev.news.controller.api.UserResourcesApi;
import pl.dev.news.devnewsservice.service.UserResourcesService;
import pl.dev.news.devnewsservice.utils.HeaderUtils;
import pl.dev.news.model.rest.RestPostModel;
import pl.dev.news.model.rest.RestPostQueryParameters;
import pl.dev.news.model.rest.RestUserModel;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class UserResourcesController implements UserResourcesApi {

    private final UserResourcesService userResourcesService;

    @Override
    public ResponseEntity<List<RestPostModel>> getUserBookmarks(
            @PathVariable("userId") final UUID userId,
            @Valid final RestPostQueryParameters parameters,
            @Min(1) @Valid
            @RequestParam(value = "page", required = false, defaultValue = "1") final Integer page,
            @Min(10) @Max(30) @Valid
            @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size
    ) {
        final Page<RestPostModel> users = userResourcesService.getBookmarks(userId, parameters, page, size);
        final HttpHeaders headers = HeaderUtils.generatePaginationHeaders(basePath, users);
        return new ResponseEntity<>(users.getContent(), headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<RestUserModel>> getUserFollowers(
            @PathVariable("userId") final UUID userId,
            @Min(1) @Valid
            @RequestParam(value = "page", required = false, defaultValue = "1") final Integer page,
            @Min(10) @Max(30) @Valid
            @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size
    ) {
        final Page<RestUserModel> users = userResourcesService.getFollowers(userId, page, size);
        final HttpHeaders headers = HeaderUtils.generatePaginationHeaders(basePath, users);
        return new ResponseEntity<>(users.getContent(), headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<RestUserModel>> getUserFollowing(
            @PathVariable("userId") final UUID userId,
            @Min(1) @Valid
            @RequestParam(value = "page", required = false, defaultValue = "1") final Integer page,
            @Min(10) @Max(30) @Valid
            @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size
    ) {
        final Page<RestUserModel> users = userResourcesService.getFollowing(userId, page, size);
        final HttpHeaders headers = HeaderUtils.generatePaginationHeaders(basePath, users);
        return new ResponseEntity<>(users.getContent(), headers, HttpStatus.OK);
    }

}
