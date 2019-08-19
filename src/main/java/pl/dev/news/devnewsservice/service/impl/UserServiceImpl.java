package pl.dev.news.devnewsservice.service.impl;

import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dev.news.devnewsservice.entity.QUserEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.exception.NotFoundException;
import pl.dev.news.devnewsservice.mapper.UserMapper;
import pl.dev.news.devnewsservice.repository.QueryDslUserRepository;
import pl.dev.news.devnewsservice.service.UserService;
import pl.dev.news.devnewsservice.utils.QueryUtils;
import pl.dev.news.model.rest.RestUserModel;

import java.util.UUID;

import static pl.dev.news.devnewsservice.exception.ExceptionsConstants.userNotFound;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final QueryDslUserRepository userRepository;

    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Override
    @Transactional
    public void delete(final UUID userId) {
        final UserEntity userEntity = userRepository.findOne(
                userRepository.soft(QUserEntity.userEntity.id.eq(userId))
        ).orElseThrow(() -> new NotFoundException(userNotFound));
        userRepository.softDelete(userEntity.getId());
    }

    @Override
    @Transactional
    public RestUserModel get(final UUID userId) {
        final UserEntity userEntity = userRepository.findOne(
                userRepository.soft(QUserEntity.userEntity.id.eq(userId))
        ).orElseThrow(() -> new NotFoundException(userNotFound));
        return UserMapper.INSTANCE.toModel(userEntity);
    }

    @Override
    @Transactional
    public Page<RestUserModel> getUsers(
            final String username,
            final String name,
            final String email,
            final Integer page,
            final Integer size
    ) {

        final BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QueryUtils.likeIfNotNull(username, QUserEntity.userEntity.username));
        booleanBuilder.and(QueryUtils.likeIfNotNull(name, QUserEntity.userEntity.firstName));
        booleanBuilder.or(QueryUtils.likeIfNotNull(name, QUserEntity.userEntity.lastName));
        booleanBuilder.and(QueryUtils.likeIfNotNull(email, QUserEntity.userEntity.email));
        return userRepository.findAll(
                booleanBuilder,
                PageRequest.of(page - 1, size))
                .map(userMapper::toModel);
    }

}
