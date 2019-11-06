package pl.dev.news.devnewsservice.service.impl;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dev.news.devnewsservice.entity.GroupEntity;
import pl.dev.news.devnewsservice.entity.PostEntity;
import pl.dev.news.devnewsservice.entity.QPostEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.exception.NotFoundException;
import pl.dev.news.devnewsservice.mapper.PostMapper;
import pl.dev.news.devnewsservice.repository.GroupRepository;
import pl.dev.news.devnewsservice.repository.PostRepository;
import pl.dev.news.devnewsservice.repository.UserRepository;
import pl.dev.news.devnewsservice.security.SecurityResolver;
import pl.dev.news.devnewsservice.service.PostService;
import pl.dev.news.devnewsservice.utils.QueryUtils;
import pl.dev.news.model.rest.RestPostModel;
import pl.dev.news.model.rest.RestPostQueryParameters;

import java.util.UUID;

import static pl.dev.news.devnewsservice.constants.ExceptionConstants.postWithIdNotFound;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final GroupRepository groupRepository;

    private final UserRepository userRepository;

    private final SecurityResolver securityResolver;

    private final PostMapper postMapper = PostMapper.INSTANCE;

    private final QPostEntity qPostEntity = QPostEntity.postEntity;

    @Override
    @Transactional
    public RestPostModel create(final RestPostModel model) {
        final UserEntity user = securityResolver.getUser();
        final GroupEntity group = groupRepository.softFindById(model.getGroupId()).orElse(null);
        final PostEntity entity = postMapper.toEntity(model, user, group);
        final PostEntity persist = postRepository.save(entity);
        postMapper.update(persist, model);
        final PostEntity saved = postRepository.saveAndFlush(persist);
        return postMapper.toModel(saved);
    }

    @Override
    @Transactional
    public void delete(final UUID postId) {
        if (!postRepository.softExistsById(postId)) {
            throw new NotFoundException(postWithIdNotFound, postId);
        }
        postRepository.softDeleteById(postId);
    }

    @Override
    @Transactional
    public RestPostModel retrieve(final UUID postId) {
        final PostEntity entity = postRepository.softFindById(postId)
                .orElseThrow(() -> new NotFoundException(postWithIdNotFound, postId));
        return postMapper.toModel(entity);
    }

    @Override
    @Transactional
    public Page retrieveAll(
            final RestPostQueryParameters parameters,
            final Integer page,
            final Integer size
    ) {
        final Predicate predicate = new QueryUtils()
                .andLikeAny(parameters.getTitle(), qPostEntity.title)
                .andLikeAny(parameters.getText(), qPostEntity.text)
                .andSetEq(parameters.getTagId(), qPostEntity.tags.any().id)
                .andSetEq(parameters.getCategoryId(), qPostEntity.categories.any().id)
                .andEq(parameters.getGroupId(), qPostEntity.group.id)
                .andEq(parameters.getPublisherId(), qPostEntity.publisherId)
                .build();
        return postRepository.findAll(
                predicate,
                PageRequest.of(page - 1, size)
        ).map(postMapper::toModel);
    }

    @Override
    @Transactional
    public RestPostModel update(final UUID postId, final RestPostModel model) {
        final PostEntity entity = postRepository.softFindById(postId)
                .orElseThrow(() -> new NotFoundException(postWithIdNotFound, postId));
        postMapper.update(entity, model);
        final PostEntity saved = postRepository.saveAndFlush(entity);
        return postMapper.toModel(saved);
    }

    @Override
    @Transactional
    public void bookmark(final UUID postId) {
        final UserEntity user = securityResolver.getUser();
        final PostEntity entity = postRepository.softFindById(postId)
                .orElseThrow(() -> new NotFoundException(postWithIdNotFound, postId));
        user.addBookmark(entity);
        userRepository.saveAndFlush(user);

    }

    @Override
    @Transactional
    public void unBookmark(final UUID postId) {
        final UserEntity user = securityResolver.getUser();
        final PostEntity entity = postRepository.softFindById(postId)
                .orElseThrow(() -> new NotFoundException(postWithIdNotFound, postId));
        user.removeBookmark(entity);
        userRepository.saveAndFlush(user);
    }
}
