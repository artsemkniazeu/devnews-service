package pl.dev.news.devnewsservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pl.dev.news.devnewsservice.entity.GroupEntity;
import pl.dev.news.devnewsservice.entity.UploadEntity;
import pl.dev.news.model.rest.RestGroupModel;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GroupMapper {

    GroupMapper INSTANCE = Mappers.getMapper(GroupMapper.class);

    @Mapping(target = "id", ignore = true)
    GroupEntity toEntity(RestGroupModel model);

    RestGroupModel toModel(GroupEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bgUrl", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    void update(@MappingTarget GroupEntity entity, RestGroupModel model);


    @Mapping(target = "id", ignore = true)
    @Mapping(source = "uploadEntity", target = "image")
    @Mapping(source = "uploadEntity.url", target = "imageUrl")
    void updateImage(@MappingTarget GroupEntity entity, UploadEntity uploadEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "uploadEntity", target = "bg")
    @Mapping(source = "uploadEntity.url", target = "bgUrl")
    void updateBackground(@MappingTarget GroupEntity entity, UploadEntity uploadEntity);
}
