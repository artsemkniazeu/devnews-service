package pl.dev.news.devnewsservice.service;

import org.springframework.data.domain.Page;
import pl.dev.news.model.rest.RestCommentModel;
import pl.dev.news.model.rest.RestCommentQueryParameters;

import java.util.UUID;

public interface CommentService {

    RestCommentModel create(RestCommentModel model);

    void delete(UUID commentId);

    Page<RestCommentModel> retrieveAll(RestCommentQueryParameters parameters, Integer page, Integer size);

    RestCommentModel retrieve(UUID commentId);

    RestCommentModel update(UUID commentId, RestCommentModel model);
}
