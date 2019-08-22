package pl.dev.news.devnewsservice.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pl.dev.news.devnewsservice.entity.CategoryEntity;
import pl.dev.news.devnewsservice.entity.TagEntity;
import pl.dev.news.model.rest.RestTagModel;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

    TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);


    @Mapping(target = "id", ignore = true)
    TagEntity toEntity(RestTagModel model);

    RestTagModel toModel(TagEntity entity);

    @Mapping(target = "id", ignore = true)
    TagEntity update(@MappingTarget TagEntity entity, RestTagModel restTagModel);


    @AfterMapping
    default void after(@MappingTarget final TagEntity entity, final RestTagModel model) {
        if (entity == null || model == null) {
            return;
        }
        if (model.getCategoryId() != null) {
            final CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setId(model.getCategoryId());
            entity.setCategory(categoryEntity);
        }
    }

}
