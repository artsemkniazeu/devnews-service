package pl.dev.news.devnewsservice.service.impl;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dev.news.devnewsservice.entity.QTagEntity;
import pl.dev.news.devnewsservice.entity.TagEntity;
import pl.dev.news.devnewsservice.exception.NotFoundException;
import pl.dev.news.devnewsservice.mapper.TagMapper;
import pl.dev.news.devnewsservice.repository.TagRepository;
import pl.dev.news.devnewsservice.service.TagService;
import pl.dev.news.devnewsservice.utils.QueryUtils;
import pl.dev.news.model.rest.RestTagModel;
import pl.dev.news.model.rest.RestTagQueryParameters;

import java.util.UUID;

import static pl.dev.news.devnewsservice.constants.ExceptionConstants.tagWithIdNotFound;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    private final TagMapper tagMapper = TagMapper.INSTANCE;

    private final QTagEntity qTagEntity = QTagEntity.tagEntity;

    @Override
    @Transactional
    public RestTagModel create(final RestTagModel restTagModel) {
        final TagEntity mapped = tagMapper.toEntity(restTagModel);
        final TagEntity saved = tagRepository.saveAndFlush(mapped);
        return tagMapper.toModel(saved);
    }

    @Override
    @Transactional
    public void delete(final UUID tagId) {
        if (!tagRepository.softExistsById(tagId)) {
            throw new NotFoundException(tagWithIdNotFound);
        }
        tagRepository.softDelete(tagId);
    }

    @Override
    @Transactional
    public RestTagModel retrieve(final UUID tagId) {
        final TagEntity entity = tagRepository.softFindById(tagId)
                .orElseThrow(() -> new NotFoundException(tagWithIdNotFound));
        return tagMapper.toModel(entity);
    }

    @Override
    @Transactional
    public Page<RestTagModel> retrieveAll(
            final RestTagQueryParameters parameters,
            final Integer page, final Integer size
    ) {
        final Predicate predicate = new QueryUtils()
                .like(parameters.getName(), qTagEntity.name) // TODO add all parameters
                .build();
        return tagRepository.findAll(
                predicate,
                PageRequest.of(page - 1, size)
        ).map(tagMapper::toModel);
    }

    @Override
    @Transactional
    public RestTagModel update(final UUID tagId, final RestTagModel restTagModel) {
        final TagEntity entity = tagRepository.softFindById(tagId)
                .orElseThrow(() -> new NotFoundException(tagWithIdNotFound));
        tagMapper.update(entity, restTagModel);
        final TagEntity saved = tagRepository.saveAndFlush(entity);
        return tagMapper.toModel(saved);
    }
}
