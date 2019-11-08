package pl.dev.news.devnewsservice.service.impl;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.dev.news.devnewsservice.entity.GroupEntity;
import pl.dev.news.devnewsservice.entity.QGroupEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.exception.NotFoundException;
import pl.dev.news.devnewsservice.mapper.GroupMapper;
import pl.dev.news.devnewsservice.repository.GroupRepository;
import pl.dev.news.devnewsservice.security.SecurityResolver;
import pl.dev.news.devnewsservice.service.GroupService;
import pl.dev.news.devnewsservice.utils.QueryUtils;
import pl.dev.news.model.rest.RestGroupModel;
import pl.dev.news.model.rest.RestGroupQueryParameters;

import java.util.UUID;

import static pl.dev.news.devnewsservice.constants.ExceptionConstants.groupWithIdNotFound;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final SecurityResolver securityResolver;
    private final GroupMapper groupMapper = GroupMapper.INSTANCE;
    private final QGroupEntity qGroupEntity = QGroupEntity.groupEntity;

    @Override
    public RestGroupModel create(final RestGroupModel model) {
        final UserEntity user = securityResolver.getUser();
        final GroupEntity entity = groupMapper.toEntity(model);
        entity.setOwner(user);
        final GroupEntity saved = groupRepository.saveAndFlush(entity);
        return groupMapper.toModel(saved);
    }

    @Override
    public void delete(final UUID groupId) {
        if (!groupRepository.softExistsById(groupId)) {
            throw new NotFoundException(groupWithIdNotFound, groupId);
        }
        groupRepository.softDeleteById(groupId);
    }

    @Override
    public RestGroupModel retrieve(final UUID groupId) {
        final GroupEntity entity = groupRepository.softFindById(groupId)
                .orElseThrow(() -> new NotFoundException(groupWithIdNotFound, groupId));
        return groupMapper.toModel(entity);
    }

    @Override
    public Page<RestGroupModel> retrieveAll(
            final RestGroupQueryParameters parameters,
            final Integer page,
            final Integer size
    ) {
        final Predicate predicate = new QueryUtils()
                .andLikeAny(parameters.getName(), qGroupEntity.name)
                .andLikeAny(parameters.getValue(), qGroupEntity.value)
                .andEq(parameters.getOwnerId(), qGroupEntity.ownerId)
                .andEq(parameters.getUserId(), qGroupEntity.followers.any().id)
                .build();
        return groupRepository.findAll(
                predicate,
                PageRequest.of(page - 1, size)
        ).map(groupMapper::toModel);
    }

    @Override
    public RestGroupModel update(final UUID groupId, final RestGroupModel model) {
        final GroupEntity entity = groupRepository.softFindById(groupId)
                .orElseThrow(() -> new NotFoundException(groupWithIdNotFound, groupId));
        groupMapper.update(entity, model);
        final GroupEntity saved = groupRepository.saveAndFlush(entity);
        return groupMapper.toModel(saved);
    }
}
