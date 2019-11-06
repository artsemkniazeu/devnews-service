package pl.dev.news.devnewsservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.dev.news.controller.api.PostApi;
import pl.dev.news.devnewsservice.service.PostService;
import pl.dev.news.devnewsservice.utils.HeaderUtils;
import pl.dev.news.model.rest.RestPostModel;
import pl.dev.news.model.rest.RestPostQueryParameters;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PostController implements PostApi {

    private final PostService postService;

    @Override
    public ResponseEntity<RestPostModel> createPost(@Valid @RequestBody final RestPostModel restPostModel) {
        final RestPostModel model = postService.create(restPostModel);
        final HttpHeaders headers = HeaderUtils.generateLocationHeader(getPostPath, model.getId());
        return new ResponseEntity<>(model, headers, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deletePost(@PathVariable("postId") final UUID postId) {
        postService.delete(postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<RestPostModel> getPost(@PathVariable("postId") final UUID postId) {
        final RestPostModel model = postService.retrieve(postId);
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<RestPostModel>> getPosts(
            @Valid final RestPostQueryParameters parameters,
            @Min(1)  @Valid @RequestParam(value = "page", required = false, defaultValue = "1") final Integer page,
            @Min(10) @Max(30) @Valid
            @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size
    ) {
        final Page<RestPostModel> posts = postService.retrieveAll(parameters, page, size);
        final HttpHeaders headers = HeaderUtils.generatePaginationHeaders(getPostsPath, posts);
        return new ResponseEntity<>(posts.getContent(), headers, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<Void> bookmarkPost(@PathVariable("postId") final UUID postId) {
        postService.bookmark(postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> unBookmarkPost(@PathVariable("postId") final UUID postId) {
        postService.unBookmark(postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RestPostModel> updatePost(
            @PathVariable("postId") final UUID postId,
            @Valid @RequestBody final RestPostModel restPostModel
    ) {
        final RestPostModel model = postService.update(postId, restPostModel);
        return new ResponseEntity<>(model, HttpStatus.OK);
    }


}
