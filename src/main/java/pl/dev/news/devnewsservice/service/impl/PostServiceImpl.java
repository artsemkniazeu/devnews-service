package pl.dev.news.devnewsservice.service.impl;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.dev.news.devnewsservice.entity.PostEntity;
import pl.dev.news.devnewsservice.entity.QPostEntity;
import pl.dev.news.devnewsservice.exception.NotFoundException;
import pl.dev.news.devnewsservice.mapper.PostMapper;
import pl.dev.news.devnewsservice.repository.PostRepository;
import pl.dev.news.devnewsservice.service.PostService;
import pl.dev.news.devnewsservice.utils.QueryUtils;
import pl.dev.news.model.rest.RestPostModel;

import java.util.UUID;

import static pl.dev.news.devnewsservice.constants.ExceptionConstants.postWithIdNotFound;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final PostMapper postMapper = PostMapper.INSTANCE;

    private final QPostEntity qPostEntity = QPostEntity.postEntity;

    @Override
    public RestPostModel create(final RestPostModel restPostModel) {
        final PostEntity entity = postMapper.toEntity(restPostModel);
        final PostEntity saved = postRepository.saveAndFlush(entity);
        return postMapper.toModel(saved);
    }

    @Override
    public void delete(final UUID postId) {
        if (!postRepository.softExistsById(postId)) {
            throw new NotFoundException(postWithIdNotFound);
        }
        postRepository.softDelete(postId);
    }

    @Override
    public RestPostModel retrieve(final UUID postId) {
        final PostEntity entity = postRepository.softFindById(postId)
                .orElseThrow(() -> new NotFoundException(postWithIdNotFound));
        return postMapper.toModel(entity);
    }

    @Override
    public Page retrieveAll(
            final UUID publisherId,
            final String title,
            final String text,
            final Integer page,
            final Integer size
    ) {
        final Predicate predicate = new QueryUtils()
                .like(title, qPostEntity.title)
                .like(text, qPostEntity.text)
                .build();
        return postRepository.findAll(
                predicate,
                PageRequest.of(page - 1, size)
        ).map(postMapper::toModel);
    }

    @Override
    public RestPostModel update(final UUID postId, final RestPostModel restPostModel) {
        final PostEntity entity = postRepository.softFindById(postId)
                .orElseThrow(() -> new NotFoundException(postWithIdNotFound));
        postMapper.update(entity, restPostModel);
        final PostEntity saved = postRepository.saveAndFlush(entity);
        return postMapper.toModel(saved);
    }
}
