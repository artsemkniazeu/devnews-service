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
import pl.dev.news.controller.api.GroupApi;
import pl.dev.news.devnewsservice.service.GroupService;
import pl.dev.news.devnewsservice.utils.HeaderUtils;
import pl.dev.news.model.rest.RestGroupModel;
import pl.dev.news.model.rest.RestGroupQueryParameters;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GroupController implements GroupApi {

    private final GroupService groupService;

    @Override
    public ResponseEntity<RestGroupModel> createGroup(@Valid @RequestBody final RestGroupModel restGroupModel) {
        final RestGroupModel model = groupService.create(restGroupModel);
        final HttpHeaders headers = HeaderUtils.generateLocationHeader(getGroupPath, model.getId());
        return new ResponseEntity<>(model, headers, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteGroup(@PathVariable("groupId") final UUID groupId) {
        groupService.delete(groupId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<RestGroupModel> getGroup(@PathVariable("groupId") final UUID groupId) {
        final RestGroupModel model = groupService.retrieve(groupId);
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<RestGroupModel>> getGroups(
            @Valid final RestGroupQueryParameters parameters,
            @Min(1) @Valid
            @RequestParam(value = "page", required = false, defaultValue = "1") final Integer page,
            @Min(10) @Max(30) @Valid
            @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size
    ) {
        final Page<RestGroupModel> groups = groupService.retrieveAll(parameters, page, size);
        final HttpHeaders headers = HeaderUtils.generatePaginationHeaders(getGroupsPath, groups);
        return new ResponseEntity<>(groups.getContent(), headers, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<RestGroupModel> updateGroup(
            @PathVariable("groupId") final UUID groupId,
            @Valid @RequestBody final RestGroupModel restGroupModel
    ) {
        final RestGroupModel model = groupService.update(groupId, restGroupModel);
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

}
