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
import pl.dev.news.controller.api.CommentApi;
import pl.dev.news.devnewsservice.service.CommentService;
import pl.dev.news.devnewsservice.utils.HeaderUtils;
import pl.dev.news.model.rest.RestCommentModel;
import pl.dev.news.model.rest.RestCommentQueryParameters;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CommentController implements CommentApi {

    private final CommentService commentService;


    @Override
    public ResponseEntity<RestCommentModel> createComment(@Valid @RequestBody final RestCommentModel restCommentModel) {
        final RestCommentModel categoryModel = commentService.create(restCommentModel);
        final HttpHeaders headers = HeaderUtils.generateLocationHeader(getCommentPath, categoryModel.getId());
        return new ResponseEntity<>(categoryModel, headers, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") final UUID commentId) {
        commentService.delete(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<RestCommentModel> getComment(@PathVariable("commentId") final UUID commentId) {
        final RestCommentModel categoryModel = commentService.retrieve(commentId);
        return new ResponseEntity<>(categoryModel, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<RestCommentModel>> getComments(
            @Valid final RestCommentQueryParameters parameters,
            @Min(1) @Valid
            @RequestParam(value = "page", required = false, defaultValue = "1") final Integer page,
            @Min(10) @Max(30) @Valid
            @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size
    ) {
        final Page<RestCommentModel> categories = commentService.retrieveAll(parameters, page, size);
        final HttpHeaders headers = HeaderUtils.generatePaginationHeaders(getCommentsPath, categories);
        return new ResponseEntity<>(categories.getContent(), headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RestCommentModel> updateComment(
            @PathVariable("commentId") final UUID commentId,
            @Valid @RequestBody final RestCommentModel restCommentModel
    ) {
        final RestCommentModel categoryModel = commentService.update(commentId, restCommentModel);
        return new ResponseEntity<>(categoryModel, HttpStatus.OK);
    }

}
