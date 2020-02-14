package pl.dev.news.devnewsservice.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pl.dev.news.devnewsservice.entity.CommentEntity;
import pl.dev.news.model.rest.RestCommentModel;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    CommentEntity toEntity(RestCommentModel model);

    RestCommentModel toModel(CommentEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "postId", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "parentId", ignore = true)
    void update(@MappingTarget CommentEntity entity, RestCommentModel model);

    @AfterMapping
    default void after(@MappingTarget final CommentEntity entity, final RestCommentModel model) {
        if (entity == null || model == null) {
            return;
        }
        if (model.getChildren() != null && model.getChildren().size() > 0) {
            entity.getChildren().forEach(c -> c.setParent(entity));
        }
        if (model.getLikes() == null) {
            entity.setLikes(0L);
        }
    }
}
