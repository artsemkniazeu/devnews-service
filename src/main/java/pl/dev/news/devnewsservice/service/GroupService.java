package pl.dev.news.devnewsservice.service;

import org.springframework.data.domain.Page;
import pl.dev.news.model.rest.RestGroupModel;
import pl.dev.news.model.rest.RestGroupQueryParameters;

import java.util.UUID;

public interface GroupService {

    RestGroupModel create(RestGroupModel model);

    void delete(UUID groupId);

    RestGroupModel retrieve(UUID groupId);

    Page<RestGroupModel> retrieveAll(RestGroupQueryParameters parameters, Integer page, Integer size);

    RestGroupModel update(UUID groupId, RestGroupModel model);
}
