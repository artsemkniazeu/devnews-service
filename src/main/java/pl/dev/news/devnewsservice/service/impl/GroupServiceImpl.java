package pl.dev.news.devnewsservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dev.news.devnewsservice.entity.GroupEntity;
import pl.dev.news.devnewsservice.entity.QGroupEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.exception.ConflictException;
import pl.dev.news.devnewsservice.exception.NotFoundException;
import pl.dev.news.devnewsservice.mapper.GroupMapper;
import pl.dev.news.devnewsservice.repository.GroupRepository;
import pl.dev.news.devnewsservice.security.SecurityResolver;
import pl.dev.news.devnewsservice.service.GroupService;
import pl.dev.news.devnewsservice.utils.CommonUtils;
import pl.dev.news.model.rest.RestGroupModel;
import pl.dev.news.model.rest.RestGroupQueryParameters;
import pl.dev.news.model.rest.RestIdModel;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static pl.dev.news.devnewsservice.constants.ExceptionConstants.groupWithIdNotFound;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.groupWithValueAlreadyExists;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final SecurityResolver securityResolver;
    private final GroupMapper groupMapper = GroupMapper.INSTANCE;
    private final QGroupEntity qGroupEntity = QGroupEntity.groupEntity;

    @Override
    @Transactional
    public RestGroupModel create(final RestGroupModel model) {
        if (groupRepository.exists(qGroupEntity.value.eq(model.getValue()))) {
            throw new ConflictException(groupWithValueAlreadyExists, model.getValue());
        }
        final UserEntity user = securityResolver.getUser();

        final GroupEntity entity = groupMapper.toEntity(model);
        entity.setOwner(user);
        final GroupEntity saved = groupRepository.saveAndFlush(entity);
        return groupMapper.toModel(saved);
    }

    @Override
    @Transactional
    public void delete(final UUID groupId) {
        if (!groupRepository.softExistsById(groupId)) {
            throw new NotFoundException(groupWithIdNotFound, groupId);
        }
        groupRepository.softDeleteById(groupId);
    }

    @Override
    @Transactional
    public RestGroupModel retrieve(final UUID groupId) {
        final GroupEntity entity = groupRepository.softFindById(groupId)
                .orElseThrow(() -> new NotFoundException(groupWithIdNotFound, groupId));
        return groupMapper.toModel(entity);
    }

    @Override
    @Transactional
    public Page<RestGroupModel> find(
            final RestGroupQueryParameters parameters,
            final Integer page,
            final Integer size
    ) {
        return groupRepository.find(
                parameters.getGroupId(),
                parameters.getUserId(),
                parameters.getOwnerId(),
                CommonUtils.nullSafeToString(parameters.getName()),
                CommonUtils.nullSafeToString(parameters.getValue()),
                PageRequest.of(page - 1, size)
        ).map(groupMapper::toModel);
    }

    @Override
    @Transactional
    public RestGroupModel update(final UUID groupId, final RestGroupModel model) {
        final GroupEntity entity = groupRepository.softFindById(groupId)
                .orElseThrow(() -> new NotFoundException(groupWithIdNotFound, groupId));
        groupMapper.update(entity, model);
        final GroupEntity saved = groupRepository.saveAndFlush(entity);
        return groupMapper.toModel(saved);
    }

    @Override
    @Transactional
    public void unfollowMultiple(final List<RestIdModel> restIds) {
        final UserEntity user = securityResolver.getUser();
        final List<UUID> ids = restIds.stream()
                .map(RestIdModel::getId)
                .collect(Collectors.toList());
        groupRepository.unFollowAll(ids, user.getId());
    }

    @Override
    public void follow(final UUID groupId) {
        final UserEntity follower = securityResolver.getUser();
        final GroupEntity groupEntity = groupRepository.softFindById(groupId)
                .orElseThrow(() -> new NotFoundException(groupWithIdNotFound, groupId));
        groupEntity.addFollower(follower);
        groupRepository.saveAndFlush(groupEntity);
    }

    @Override
    public void unfollow(final UUID groupId) {
        final UserEntity follower = securityResolver.getUser();
        final GroupEntity groupEntity = groupRepository.softFindById(groupId)
                .orElseThrow(() -> new NotFoundException(groupWithIdNotFound, groupId));
        groupEntity.removeFollower(follower);
        groupRepository.saveAndFlush(groupEntity);
    }

}
