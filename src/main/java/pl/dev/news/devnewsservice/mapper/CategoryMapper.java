package pl.dev.news.devnewsservice.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pl.dev.news.devnewsservice.entity.CategoryEntity;
import pl.dev.news.model.rest.RestCategoryModel;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Mapping(target = "id", ignore = true)
    CategoryEntity toEntity(RestCategoryModel model);

    RestCategoryModel toModel(CategoryEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "children", ignore = true)
    void update(@MappingTarget CategoryEntity entity, RestCategoryModel model);

    @AfterMapping
    default void after(@MappingTarget final CategoryEntity entity, final RestCategoryModel model) {
        if (entity == null || model == null) {
            return;
        }
        if (model.getChildren() != null && model.getChildren().size() > 0) {
            entity.getChildren().forEach(c -> c.setParent(entity));
        }
    }
}
