package pl.dev.news.devnewsservice.service.impl;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.dev.news.devnewsservice.entity.QTagEntity;
import pl.dev.news.devnewsservice.entity.TagEntity;
import pl.dev.news.devnewsservice.exception.NotFoundException;
import pl.dev.news.devnewsservice.mapper.TagMapper;
import pl.dev.news.devnewsservice.repository.TagRepository;
import pl.dev.news.devnewsservice.service.TagService;
import pl.dev.news.devnewsservice.utils.QueryUtils;
import pl.dev.news.model.rest.RestTagModel;

import java.util.UUID;

import static pl.dev.news.devnewsservice.constants.ExceptionConstants.tagWithIdNotExists;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    private final TagMapper tagMapper = TagMapper.INSTANCE;

    @Override
    public RestTagModel createTag(final RestTagModel restTagModel) {
        final TagEntity mapped = tagMapper.toEntity(restTagModel);
        final TagEntity saved = tagRepository.saveAndFlush(mapped);
        return tagMapper.toModel(saved);
    }

    @Override
    public void deleteTag(final UUID tagId) {
        final TagEntity entity = tagRepository.findOne(
                tagRepository.soft(QTagEntity.tagEntity.id.eq(tagId))
        ).orElseThrow(() -> new NotFoundException(tagWithIdNotExists));
        tagRepository.softDelete(entity.getId());
    }

    @Override
    public RestTagModel retrieveTag(final UUID tagId) {
        final TagEntity entity = tagRepository.findOne(
                tagRepository.soft(QTagEntity.tagEntity.id.eq(tagId))
        ).orElseThrow(() -> new NotFoundException(tagWithIdNotExists));
        return tagMapper.toModel(entity);
    }

    @Override
    public Page retrieveAll(final String name, final Integer page, final Integer size) {
        final BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QueryUtils.likeIfNotNull(name, QTagEntity.tagEntity.name));
        return tagRepository.findAll(
                booleanBuilder,
                PageRequest.of(page - 1, size))
                .map(tagMapper::toModel);
    }

    @Override
    public RestTagModel updateTag(final UUID tagId, final RestTagModel restTagModel) {
        final TagEntity entity = tagRepository.findOne(
                tagRepository.soft(QTagEntity.tagEntity.id.eq(tagId))
        ).orElseThrow(() -> new NotFoundException(tagWithIdNotExists));
        final TagEntity updated = tagMapper.update(entity, restTagModel);
        final TagEntity saved = tagRepository.saveAndFlush(updated);
        return tagMapper.toModel(saved);
    }
}
