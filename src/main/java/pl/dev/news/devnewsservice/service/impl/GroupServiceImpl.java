package pl.dev.news.devnewsservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.dev.news.devnewsservice.config.AppConfiguration;
import pl.dev.news.devnewsservice.entity.GroupEntity;
import pl.dev.news.devnewsservice.entity.QGroupEntity;
import pl.dev.news.devnewsservice.entity.UploadEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.exception.BadRequestException;
import pl.dev.news.devnewsservice.exception.ConflictException;
import pl.dev.news.devnewsservice.exception.NotFoundException;
import pl.dev.news.devnewsservice.mapper.GroupMapper;
import pl.dev.news.devnewsservice.mapper.UploadMapper;
import pl.dev.news.devnewsservice.repository.GroupRepository;
import pl.dev.news.devnewsservice.security.SecurityResolver;
import pl.dev.news.devnewsservice.service.GroupService;
import pl.dev.news.devnewsservice.service.UploadService;
import pl.dev.news.devnewsservice.utils.CommonUtils;
import pl.dev.news.devnewsservice.utils.ImageUtils;
import pl.dev.news.model.rest.RestGroupModel;
import pl.dev.news.model.rest.RestGroupQueryParameters;
import pl.dev.news.model.rest.RestIdModel;
import pl.dev.news.model.rest.RestUploadModel;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static pl.dev.news.devnewsservice.constants.ExceptionConstants.groupWithIdNotFound;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.groupWithValueAlreadyExists;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.invalidImageFormat;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final AppConfiguration appConfiguration;

    private final GroupRepository groupRepository;

    private final SecurityResolver securityResolver;

    private final UploadService uploadService;

    private final GroupMapper groupMapper = GroupMapper.INSTANCE;

    private final UploadMapper uploadMapper = UploadMapper.INSTANCE;

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


    @Override
    @Transactional
    public RestUploadModel uploadImage(final UUID groupId, final MultipartFile file) {
        final UserEntity userEntity = securityResolver.getUser();
        final GroupEntity entity = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException(groupWithIdNotFound, groupId));
        final UploadEntity saved = uploadImage(userEntity, entity.getImage(), file);
        groupMapper.updateImage(entity, saved);
        groupRepository.saveAndFlush(entity);
        return uploadMapper.toModel(saved);
    }

    @Override
    @Transactional
    public RestUploadModel uploadBackground(final UUID userId, final MultipartFile file) {
        final UserEntity userEntity = securityResolver.getUser();
        final GroupEntity entity = groupRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(groupWithIdNotFound, userId));
        final UploadEntity saved = uploadImage(userEntity, entity.getBg(), file);
        groupMapper.updateBackground(entity, saved);
        groupRepository.saveAndFlush(entity);
        return uploadMapper.toModel(saved);
    }

    private UploadEntity uploadImage(final UserEntity entity, final UploadEntity old, final MultipartFile file) {
        if (!ImageUtils.isValid(file)) {
            throw new BadRequestException(invalidImageFormat);
        }
        if (old != null) {
            uploadService.delete(old);
        }
        return uploadService.upload(entity, file, appConfiguration.getGoogle().getStorage().getImageBucket());
    }

}
