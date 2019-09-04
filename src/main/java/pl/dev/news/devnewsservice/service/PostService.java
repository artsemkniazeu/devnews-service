package pl.dev.news.devnewsservice.service;

import org.springframework.data.domain.Page;
import pl.dev.news.model.rest.RestPostModel;
import pl.dev.news.model.rest.RestPostQueryParameters;

import java.util.UUID;

public interface PostService {

    RestPostModel create(RestPostModel restPostModel);

    void delete(UUID postId);

    RestPostModel retrieve(UUID postId);

    Page<RestPostModel> retrieveAll(RestPostQueryParameters parameters, Integer page, Integer size);

    RestPostModel update(UUID postId, RestPostModel restPostModel);
}
