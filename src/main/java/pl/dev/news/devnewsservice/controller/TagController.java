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
import pl.dev.news.controller.api.TagApi;
import pl.dev.news.devnewsservice.service.TagService;
import pl.dev.news.devnewsservice.utils.HeaderUtils;
import pl.dev.news.model.rest.RestTagModel;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TagController implements TagApi {

    private final TagService tagService;

    @Override
    public ResponseEntity<RestTagModel> createTag(@Valid @RequestBody final RestTagModel restTagModel) {
        final RestTagModel tagModel = tagService.createTag(restTagModel);
        final HttpHeaders headers = HeaderUtils.generateLocationHeader(getTagPath, tagModel.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteTag(@PathVariable("tagId") final UUID tagId) {
        tagService.deleteTag(tagId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<RestTagModel> getTag(@PathVariable("tagId") final UUID tagId) {
        final RestTagModel tagModel = tagService.retrieveTag(tagId);
        return new ResponseEntity<>(tagModel, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<RestTagModel>> getTags(
            @Valid
            @RequestParam(value = "name", required = false) final String name,
            @Min(1) @Valid
            @RequestParam(value = "page", required = false, defaultValue = "1") final Integer page,
            @Min(10) @Max(30) @Valid
            @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size
    ) {
        final Page tags = tagService.retrieveAll(name, page, size);
        final HttpHeaders headers = HeaderUtils.generatePaginationHeaders(getTagsPath, tags);
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RestTagModel> updateTag(
            @PathVariable("tagId") final UUID tagId,
            @Valid @RequestBody final RestTagModel restTagModel
    ) {
        final RestTagModel tagModel = tagService.updateTag(tagId, restTagModel);
        return new ResponseEntity<>(tagModel, HttpStatus.OK);
    }


}
