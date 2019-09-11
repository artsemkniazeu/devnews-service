package pl.dev.news.devnewsservice.repository;

import org.springframework.stereotype.Repository;
import pl.dev.news.devnewsservice.entity.QTagEntity;
import pl.dev.news.devnewsservice.entity.TagEntity;
import pl.dev.news.devnewsservice.repository.custom.CustomQueryDslRspository;

import java.util.UUID;

@Repository
public interface TagRepository extends CustomQueryDslRspository<TagEntity, QTagEntity, UUID> {

}
