package pl.dev.news.devnewsservice.service.impl;

import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.dev.news.devnewsservice.entity.QUserEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.exception.NotFoundException;
import pl.dev.news.devnewsservice.mapper.UserMapper;
import pl.dev.news.devnewsservice.repository.UserRepository;
import pl.dev.news.devnewsservice.service.UserService;
import pl.dev.news.devnewsservice.utils.QueryUtils;
import pl.dev.news.model.rest.RestUploadModel;
import pl.dev.news.model.rest.RestUserModel;
import pl.dev.news.model.rest.RestUserQueryParameters;

import java.util.UUID;

import static pl.dev.news.devnewsservice.constants.ExceptionConstants.userWithIdNotFound;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper = UserMapper.INSTANCE;

    private final QUserEntity qUserEntity = QUserEntity.userEntity;

    @Override
    @Transactional
    public void delete(final UUID userId) {
        if (!userRepository.softExistsById(userId)) {
            throw new NotFoundException(userWithIdNotFound, userId);
        }
        userRepository.softDeleteById(userId);
    }

    @Override
    @Transactional
    public RestUserModel get(final UUID userId) {
        final UserEntity userEntity = userRepository.softFindById(userId)
                .orElseThrow(() -> new NotFoundException(userWithIdNotFound, userId));
        return UserMapper.INSTANCE.toModel(userEntity);
    }

    @Override
    @Transactional
    public Page<RestUserModel> getUsers(
            final RestUserQueryParameters parameters,
            final Integer page,
            final Integer size
    ) {
        final Predicate predicate = new QueryUtils()
                .andLikeAny(parameters.getName(), qUserEntity.firstName, qUserEntity.lastName)
                .andLikeAny(parameters.getUsername(), qUserEntity.username)
                .andLikeAny(parameters.getEmail(), qUserEntity.email)
                .build();
        return userRepository.findAll(
                predicate,
                PageRequest.of(page - 1, size)
        ).map(userMapper::toModel);
    }

    @Override
    @Transactional
    public RestUserModel update(final UUID userId, final RestUserModel restUserModel) {
        final UserEntity entity = userRepository.softFindById(userId)
                .orElseThrow(() -> new NotFoundException(userWithIdNotFound, userId));
        userMapper.update(entity, restUserModel);
        final UserEntity saved = userRepository.saveAndFlush(entity);
        return userMapper.toModel(saved);
    }

    @Override
    public RestUploadModel uploadImage(final UUID userId, final MultipartFile file) {
        return null;
    }

}
