package pl.dev.news.devnewsservice.mapper.context;

import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import pl.dev.news.devnewsservice.entity.GroupEntity;
import pl.dev.news.devnewsservice.entity.PostEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.model.rest.RestPostModel;

public interface PostMapperContext {

    @AfterMapping
    default void after(
            @MappingTarget final PostEntity entity,
            final RestPostModel model
    ) {

    }

    @AfterMapping
    default void after(
            @MappingTarget final PostEntity entity,
            final RestPostModel model,
            final UserEntity user,
            final GroupEntity group
    ){

    }

}
