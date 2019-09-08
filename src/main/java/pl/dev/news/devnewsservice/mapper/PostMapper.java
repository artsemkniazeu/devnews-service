package pl.dev.news.devnewsservice.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pl.dev.news.devnewsservice.entity.GroupEntity;
import pl.dev.news.devnewsservice.entity.PostEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.mapper.context.DefaultPostMapperContext;
import pl.dev.news.devnewsservice.mapper.context.PostMapperContext;
import pl.dev.news.devnewsservice.mapper.context.UpdatePostMapperContext;
import pl.dev.news.model.rest.RestPostModel;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
        UserMapper.class,
        CategoryMapper.class,
        TagMapper.class,
        UploadMapper.class
})
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "uploads", ignore = true)
    @Mapping(target = "imageUrl", source = "model.imageUrl")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    PostEntity toEntity(RestPostModel model, UserEntity user, GroupEntity group, @Context PostMapperContext context);

    RestPostModel toModel(PostEntity saved);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "uploads", ignore = true)
    void update(@MappingTarget PostEntity entity, RestPostModel model, @Context PostMapperContext context);

    default PostEntity toEntity(final RestPostModel model, final UserEntity user, final GroupEntity group) {
        return toEntity(model, user, group, new DefaultPostMapperContext());
    }

    default void update(@MappingTarget final PostEntity entity, final RestPostModel model) {
        update(entity, model, new UpdatePostMapperContext());
    }

}
