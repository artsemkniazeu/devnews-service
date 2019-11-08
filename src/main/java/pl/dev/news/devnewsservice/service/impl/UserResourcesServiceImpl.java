package pl.dev.news.devnewsservice.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dev.news.devnewsservice.entity.QPostEntity;
import pl.dev.news.devnewsservice.entity.QUserEntity;
import pl.dev.news.devnewsservice.mapper.PostMapper;
import pl.dev.news.devnewsservice.mapper.UserMapper;
import pl.dev.news.devnewsservice.repository.PostRepository;
import pl.dev.news.devnewsservice.repository.UserRepository;
import pl.dev.news.devnewsservice.service.UserResourcesService;
import pl.dev.news.devnewsservice.utils.QueryUtils;
import pl.dev.news.model.rest.RestPostModel;
import pl.dev.news.model.rest.RestPostQueryParameters;
import pl.dev.news.model.rest.RestUserModel;
import pl.dev.news.model.rest.RestUserQueryParameters;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserResourcesServiceImpl implements UserResourcesService {

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final UserMapper userMapper = UserMapper.INSTANCE;

    private final PostMapper postMapper = PostMapper.INSTANCE;

    private final QUserEntity qUserEntity = QUserEntity.userEntity;

    private final QPostEntity qPostEntity = QPostEntity.postEntity;

    @Override
    @Transactional
    public Page<RestPostModel> getBookmarks(
            final UUID userId,
            final RestPostQueryParameters parameters,
            final Integer page,
            final Integer size
    ) {
        final Predicate predicate = new QueryUtils()
                .andEq(userId, qPostEntity.usersSaved.any().id)
                .build();
        return postRepository.findAll(
                predicate,
                PageRequest.of(page - 1, size)
        ).map(postMapper::toModel);
    }

    @Override
    @Transactional
    public Page<RestUserModel> getFollowers(
            final UUID userId,
            final RestUserQueryParameters parameters,
            final Integer page,
            final Integer size
    ) {
        final QUserEntity following = QUserEntity.userEntity.followingUsers.any();
        final Predicate predicate = new QueryUtils()
                .andEq(userId, following.id)
                .andEq(parameters.getId(), qUserEntity.id)
                .and(findByParams(parameters))
                .build();
        return userRepository.findAll(
                predicate,
                PageRequest.of(page - 1, size)
        ).map(userMapper::toModel);
    }

    @Override
    @Transactional
    public Page<RestUserModel> getFollowing(
            final UUID userId,
            final RestUserQueryParameters parameters,
            final Integer page,
            final Integer size
    ) {
        final QUserEntity followers = QUserEntity.userEntity.followers.any();
        final Predicate predicate = new QueryUtils()
                .andEq(userId, followers.id)
                .andEq(parameters.getId(), qUserEntity.id)
                .and(findByParams(parameters))
                .build();
        return userRepository.findAll(
                predicate,
                PageRequest.of(page - 1, size)
        ).map(userMapper::toModel);
    }

    private BooleanBuilder findByParams(final RestUserQueryParameters parameters) {
        return new QueryUtils()
                .orEq(parameters.getEmail(), qUserEntity.email)
                .orLikeAny(parameters.getName(), qUserEntity.fullName)
                .orLikeAny(parameters.getUsername(), qUserEntity.username)
                .getBuilder();
    }
}
