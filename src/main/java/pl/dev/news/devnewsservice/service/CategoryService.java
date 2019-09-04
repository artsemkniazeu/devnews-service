package pl.dev.news.devnewsservice.service;

import org.springframework.data.domain.Page;
import pl.dev.news.model.rest.RestCategoryModel;
import pl.dev.news.model.rest.RestCategoryQueryParameters;

import java.util.UUID;

public interface CategoryService {

    RestCategoryModel create(RestCategoryModel model);

    void delete(UUID categoryId);

    Page<RestCategoryModel> retrieveAll(RestCategoryQueryParameters parameters, Integer page, Integer size);

    RestCategoryModel retrieve(UUID categoryId);

    RestCategoryModel update(UUID categoryId, RestCategoryModel model);
}
