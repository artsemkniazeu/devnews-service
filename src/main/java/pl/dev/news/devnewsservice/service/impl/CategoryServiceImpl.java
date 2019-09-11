package pl.dev.news.devnewsservice.service.impl;

import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dev.news.devnewsservice.entity.CategoryEntity;
import pl.dev.news.devnewsservice.entity.QCategoryEntity;
import pl.dev.news.devnewsservice.exception.NotFoundException;
import pl.dev.news.devnewsservice.mapper.CategoryMapper;
import pl.dev.news.devnewsservice.repository.CategoryRepository;
import pl.dev.news.devnewsservice.service.CategoryService;
import pl.dev.news.devnewsservice.utils.QueryUtils;
import pl.dev.news.model.rest.RestCategoryModel;
import pl.dev.news.model.rest.RestCategoryQueryParameters;

import java.util.UUID;

import static pl.dev.news.devnewsservice.constants.ExceptionConstants.categoryWithIdNotFound;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper = CategoryMapper.INSTANCE;

    private final QCategoryEntity qCategoryEntity = QCategoryEntity.categoryEntity;

    @Override
    @Transactional
    public RestCategoryModel create(final RestCategoryModel model) {
        final CategoryEntity entity = categoryMapper.toEntity(model);
        final CategoryEntity saved = categoryRepository.saveAndFlush(entity);
        return categoryMapper.toModel(saved);
    }

    @Override
    @Transactional
    public void delete(final UUID categoryId) {
        if (!categoryRepository.softExistsById(categoryId)) {
            throw new NotFoundException(categoryWithIdNotFound, categoryId);
        }
        categoryRepository.softDeleteById(categoryId);
    }

    @Override
    @Transactional
    public Page<RestCategoryModel> retrieveAll(
            final RestCategoryQueryParameters parameters,
            final Integer page, final Integer size
    ) {
        final Predicate predicate = new QueryUtils()
                .andLikeAny(parameters.getName(), qCategoryEntity.name)
                .andLikeAny(parameters.getValue(), qCategoryEntity.value)
                .andEq(parameters.getParentId(), qCategoryEntity.parentId)
                .build();
        return categoryRepository.findAll(
                predicate,
                PageRequest.of(page - 1, size))
                .map(categoryMapper::toModel);
    }

    @Override
    @Transactional
    public RestCategoryModel retrieve(final UUID categoryId) {
        final CategoryEntity entity = categoryRepository.softFindById(categoryId)
                .orElseThrow(() -> new NotFoundException(categoryWithIdNotFound, categoryId));
        return categoryMapper.toModel(entity);
    }

    @Override
    @Transactional
    public RestCategoryModel update(final UUID categoryId, final RestCategoryModel model) {
        final CategoryEntity entity = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(categoryWithIdNotFound, categoryId));
        categoryMapper.update(entity, model);
        final CategoryEntity saved = categoryRepository.saveAndFlush(entity);
        return categoryMapper.toModel(saved);
    }
}
