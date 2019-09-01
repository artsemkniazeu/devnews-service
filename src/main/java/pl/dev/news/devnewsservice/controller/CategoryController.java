package pl.dev.news.devnewsservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.dev.news.controller.api.CategoryApi;
import pl.dev.news.devnewsservice.service.CategoryService;
import pl.dev.news.devnewsservice.utils.HeaderUtils;
import pl.dev.news.model.rest.RestCategoryModel;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CategoryController implements CategoryApi {

    private final CategoryService categoryService;

    @Override
    public ResponseEntity<RestCategoryModel> createCategory(
            @Valid @RequestBody final RestCategoryModel restCategoryModel
    ) {
        final RestCategoryModel categoryModel = categoryService.create(restCategoryModel);
        final HttpHeaders headers = HeaderUtils.generateLocationHeader(getCategoryPath, categoryModel.getId());
        return new ResponseEntity<>(categoryModel, headers, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteCategory(@PathVariable("categoryId") final UUID categoryId) {
        categoryService.delete(categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Override
    public ResponseEntity<List<RestCategoryModel>> getCategories(
            @Valid @RequestParam(value = "name", required = false) final String name,
            @Min(1) @Valid @RequestParam(value = "page", required = false, defaultValue = "1") final Integer page,
            @Min(10) @Max(30) @Valid
            @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size
    ) {
        final Page<RestCategoryModel> categories = categoryService.retrieveAll(name, page, size);
        final HttpHeaders headers = HeaderUtils.generatePaginationHeaders(getCategoriesPath, categories);
        return new ResponseEntity<>(categories.getContent(), headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RestCategoryModel> getCategory(@PathVariable("categoryId") final UUID categoryId) {
        final RestCategoryModel categoryModel = categoryService.retrieve(categoryId);
        return new ResponseEntity<>(categoryModel, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RestCategoryModel> updateCategory(
            @PathVariable("categoryId") final UUID categoryId,
            @Valid @RequestBody final RestCategoryModel restCategoryModel
    ) {
        final RestCategoryModel categoryModel = categoryService.update(categoryId, restCategoryModel);
        return new ResponseEntity<>(categoryModel, HttpStatus.OK);
    }

}
