package pl.dev.news.devnewsservice.service;

import org.springframework.data.domain.Page;
import pl.dev.news.model.rest.RestGroupModel;
import pl.dev.news.model.rest.RestGroupQueryParameters;
import pl.dev.news.model.rest.RestIdModel;

import java.util.List;
import java.util.UUID;

public interface GroupService {

    RestGroupModel create(RestGroupModel model);

    void delete(UUID groupId);

    RestGroupModel retrieve(UUID groupId);

    Page<RestGroupModel> find(RestGroupQueryParameters parameters, Integer page, Integer size);

    RestGroupModel update(UUID groupId, RestGroupModel model);

    void unfollowMultiple(List<RestIdModel> restIds);

    void follow(UUID groupId);

    void unfollow(UUID groupId);
}
