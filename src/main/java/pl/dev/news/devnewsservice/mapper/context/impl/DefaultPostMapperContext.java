package pl.dev.news.devnewsservice.mapper.context.impl;

import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import pl.dev.news.devnewsservice.entity.GroupEntity;
import pl.dev.news.devnewsservice.entity.PostEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.mapper.context.PostMapperContext;
import pl.dev.news.model.rest.RestPostModel;

public class DefaultPostMapperContext implements PostMapperContext {

    @Override
    @AfterMapping
    public void after(
            @MappingTarget final PostEntity entity,
            final RestPostModel model,
            final UserEntity user,
            final GroupEntity group
    ) {
        if (entity == null || model == null || user == null) {
            return;
        }
        entity.setPublisher(user);
        if (group != null) {
            entity.setGroup(group);
        }
    }

}
