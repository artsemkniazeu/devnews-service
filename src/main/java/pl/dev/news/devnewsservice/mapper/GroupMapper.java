package pl.dev.news.devnewsservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pl.dev.news.devnewsservice.entity.GroupEntity;
import pl.dev.news.model.rest.RestGroupModel;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GroupMapper {

    GroupMapper INSTANCE = Mappers.getMapper(GroupMapper.class);

    @Mapping(target = "id", ignore = true)
    GroupEntity toEntity(RestGroupModel model);

    RestGroupModel toModel(GroupEntity entity);

    @Mapping(target = "id", ignore = true)
    void update(@MappingTarget GroupEntity entity, RestGroupModel model);

}
