package pl.dev.news.devnewsservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.dev.news.controller.api.PostApi;
import pl.dev.news.model.rest.RestPostModel;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PostController implements PostApi {

    @Override
    public ResponseEntity<RestPostModel> createPost(@Valid @RequestBody final RestPostModel restPostModel) {

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deletePost(@PathVariable("postId") final UUID postId) {

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<RestPostModel> getPost(@PathVariable("postId") final UUID postId) {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<RestPostModel>> getPosts(
            @Valid @RequestParam(value = "publisher_id", required = false) final UUID publisherId,
            @Valid @RequestParam(value = "title", required = false) final String title,
            @Valid @RequestParam(value = "text", required = false) final String text,
            @Min(1)  @Valid @RequestParam(value = "page", required = false, defaultValue = "1") final Integer page,
            @Min(10) @Max(30) @Valid
            @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size
    ) {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RestPostModel> updatePost(
            @PathVariable("postId") final UUID postId,
            @Valid @RequestBody final RestPostModel restPostModel
    ) {

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
