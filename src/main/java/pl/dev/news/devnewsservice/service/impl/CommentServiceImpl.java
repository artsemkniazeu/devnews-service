package pl.dev.news.devnewsservice.service.impl;

import com.querydsl.core.types.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dev.news.devnewsservice.entity.CommentEntity;
import pl.dev.news.devnewsservice.entity.PostEntity;
import pl.dev.news.devnewsservice.entity.QCommentEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.exception.NotFoundException;
import pl.dev.news.devnewsservice.mapper.CommentMapper;
import pl.dev.news.devnewsservice.repository.CommentRepository;
import pl.dev.news.devnewsservice.repository.PostRepository;
import pl.dev.news.devnewsservice.security.SecurityResolver;
import pl.dev.news.devnewsservice.service.CommentService;
import pl.dev.news.devnewsservice.utils.QueryUtils;
import pl.dev.news.model.rest.RestCommentModel;
import pl.dev.news.model.rest.RestCommentQueryParameters;

import java.util.UUID;

import static pl.dev.news.devnewsservice.constants.ExceptionConstants.categoryWithIdNotFound;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.commentWithIdNotFound;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.postWithIdNotFound;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final CommentMapper commentMapper = CommentMapper.INSTANCE;

    private final QCommentEntity qCommentEntity = QCommentEntity.commentEntity;

    private final SecurityResolver securityResolver;

    @Override
    @Transactional
    public RestCommentModel create(final RestCommentModel model) {
        final UserEntity userEntity = securityResolver.getUser();
        final PostEntity postEntity = postRepository.softFindById(model.getPostId())
                .orElseThrow(() -> new NotFoundException(postWithIdNotFound, model.getPostId()));
        final CommentEntity entity = commentMapper.toEntity(model);
        entity.setPost(postEntity);
        entity.setUser(userEntity);
        final CommentEntity saved = commentRepository.saveAndFlush(entity);
        return commentMapper.toModel(saved);
    }

    @Override
    @Transactional
    public void delete(final UUID commentId) {
        if (!commentRepository.softExistsById(commentId)) {
            throw new NotFoundException(categoryWithIdNotFound, commentId);
        }
        commentRepository.softDeleteById(commentId);
    }

    @Override
    @Transactional
    public Page<RestCommentModel> retrieveAll(
            final RestCommentQueryParameters parameters,
            final Integer page, final Integer size
    ) {
        final Predicate predicate = new QueryUtils()
                .andLikeAny(parameters.getText(), qCommentEntity.text)
                .andEq(parameters.getParentId(), qCommentEntity.parent.id)
                .andEq(parameters.getUserId(), qCommentEntity.userId)
                .build();
        return commentRepository.findAll(
                predicate,
                PageRequest.of(page - 1, size))
                .map(commentMapper::toModel);
    }

    @Override
    @Transactional
    public RestCommentModel retrieve(final UUID commentId) {
        final CommentEntity entity = commentRepository.softFindById(commentId)
                .orElseThrow(() -> new NotFoundException(commentWithIdNotFound, commentId));
        return commentMapper.toModel(entity);
    }

    @Override
    @Transactional
    public RestCommentModel update(final UUID commentId, final RestCommentModel model) {
        final CommentEntity entity = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(commentWithIdNotFound, commentId));
        commentMapper.update(entity, model);
        final CommentEntity saved = commentRepository.saveAndFlush(entity);
        return commentMapper.toModel(saved);
    }

}
