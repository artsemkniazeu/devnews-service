package pl.dev.news.devnewsservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pl.dev.news.devnewsservice.entity.PostEntity;
import pl.dev.news.model.rest.RestPostModel;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
        UserMapper.class,
        CategoryMapper.class,
        TagMapper.class
})
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(target = "id", ignore = true)
    PostEntity toEntity(RestPostModel restPostModel);

    RestPostModel toModel(PostEntity saved);

    void update(@MappingTarget PostEntity entity, RestPostModel model);
}
