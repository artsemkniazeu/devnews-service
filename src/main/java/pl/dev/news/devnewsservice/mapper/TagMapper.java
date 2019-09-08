package pl.dev.news.devnewsservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pl.dev.news.devnewsservice.entity.TagEntity;
import pl.dev.news.model.rest.RestTagModel;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

    TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);

    TagEntity toEntity(RestTagModel model);

    RestTagModel toModel(TagEntity entity);

    @Mapping(target = "id", ignore = true)
    void update(@MappingTarget TagEntity entity, RestTagModel restTagModel);


}
