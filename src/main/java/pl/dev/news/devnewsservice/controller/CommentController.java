package pl.dev.news.devnewsservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.dev.news.controller.api.CommentApi;
import pl.dev.news.model.rest.RestCommentModel;
import pl.dev.news.model.rest.RestTagModel;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CommentController implements CommentApi {


    @Override
    public ResponseEntity<RestCommentModel> createComment(@Valid @RequestBody final RestTagModel restTagModel) {

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") final UUID commentId) {

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<RestCommentModel> getComment(@PathVariable("commentId") final UUID commentId) {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<RestCommentModel>> getComments(
            @Valid
            @RequestParam(value = "userId", required = false) final UUID userId,
            @Min(1) @Valid
            @RequestParam(value = "page", required = false, defaultValue = "1") final Integer page,
            @Min(10) @Max(30) @Valid
            @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size
    ) {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RestCommentModel> updateComment(
            @PathVariable("commentId") final UUID commentId,
            @Valid @RequestBody final RestTagModel restTagModel
    ) {

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
