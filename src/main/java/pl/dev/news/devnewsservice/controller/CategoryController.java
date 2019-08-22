package pl.dev.news.devnewsservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.dev.news.controller.api.CategoryApi;
import pl.dev.news.model.rest.RestCategoryModel;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CategoryController implements CategoryApi {

    @Override
    public ResponseEntity<RestCategoryModel> createCategory(
            @Valid @RequestBody final RestCategoryModel restCategoryModel
    ) {

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteCategory(@PathVariable("categoryId") final UUID categoryId) {

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Override
    public ResponseEntity<List<RestCategoryModel>> getCategories(
            @Valid @RequestParam(value = "name", required = false) final String name,
            @Min(1) @Valid @RequestParam(value = "page", required = false, defaultValue = "1") final Integer page,
            @Min(10) @Max(30) @Valid
            @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size
    ) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RestCategoryModel> getCategory(@PathVariable("categoryId") final UUID categoryId) {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RestCategoryModel> updateCategory(
            @PathVariable("categoryId") final UUID categoryId,
            @Valid @RequestBody final RestCategoryModel restCategoryModel
    ) {

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
