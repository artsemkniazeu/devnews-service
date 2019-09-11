package pl.dev.news.devnewsservice.service;

import org.springframework.data.domain.Page;
import pl.dev.news.model.rest.RestTagModel;
import pl.dev.news.model.rest.RestTagQueryParameters;

import java.util.UUID;

public interface TagService {

    RestTagModel create(RestTagModel restTagModel);

    void delete(UUID tagId);

    RestTagModel retrieve(UUID tagId);

    Page<RestTagModel> retrieveAll(RestTagQueryParameters parameters, Integer page, Integer size);

    RestTagModel update(UUID tagId, RestTagModel restTagModel);
}
