package pl.dev.news.devnewsservice.service;

import org.springframework.data.domain.Page;
import pl.dev.news.model.rest.RestPostModel;
import pl.dev.news.model.rest.RestPostQueryParameters;
import pl.dev.news.model.rest.RestUserModel;
import pl.dev.news.model.rest.RestUserQueryParameters;

import java.util.UUID;

public interface UserResourcesService {

    Page<RestPostModel> getBookmarks(UUID userId, RestPostQueryParameters parameters, Integer page, Integer size);

    Page<RestUserModel> getFollowers(UUID userId, RestUserQueryParameters parameters, Integer page, Integer size);

    Page<RestUserModel> getFollowing(UUID userId, RestUserQueryParameters parameters, Integer page, Integer size);

    Page<RestPostModel> getUsersFeed(UUID userId, RestPostQueryParameters parameters, Integer page, Integer size);
}
