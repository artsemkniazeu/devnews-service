package pl.dev.news.devnewsservice.service;

import org.springframework.data.domain.Page;
import pl.dev.news.model.rest.RestTagModel;

import java.util.UUID;

public interface TagService {

    RestTagModel createTag(RestTagModel restTagModel);

    void deleteTag(UUID tagId);

    RestTagModel retrieveTag(UUID tagId);

    Page retrieveAll(String name, Integer page, Integer size);

    RestTagModel updateTag(UUID tagId, RestTagModel restTagModel);
}
