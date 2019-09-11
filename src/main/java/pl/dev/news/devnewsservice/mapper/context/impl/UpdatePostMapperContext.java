package pl.dev.news.devnewsservice.mapper.context.impl;

import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import pl.dev.news.devnewsservice.entity.PostEntity;
import pl.dev.news.devnewsservice.mapper.CategoryMapper;
import pl.dev.news.devnewsservice.mapper.TagMapper;
import pl.dev.news.devnewsservice.mapper.context.PostMapperContext;
import pl.dev.news.model.rest.RestPostModel;

import java.util.HashSet;

public class UpdatePostMapperContext implements PostMapperContext {

    @Override
    @AfterMapping
    public void after(
            @MappingTarget final PostEntity entity,
            final RestPostModel model
    ) {
        if (entity == null || model == null) {
            return;
        }
        if (model.getTags() != null) {
            entity.setTags(new HashSet<>());
            model.getTags().stream()
                    .map(TagMapper.INSTANCE::toEntity)
                    .forEach(entity::addTag);
        }
        if (model.getCategories() != null) {
            entity.setCategories(new HashSet<>());
            model.getCategories().stream()
                    .map(CategoryMapper.INSTANCE::toEntity)
                    .forEach(entity::addCategory);
        }
    }

}
