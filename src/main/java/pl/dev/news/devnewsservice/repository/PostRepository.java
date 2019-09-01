package pl.dev.news.devnewsservice.repository;

import org.springframework.stereotype.Repository;
import pl.dev.news.devnewsservice.entity.PostEntity;
import pl.dev.news.devnewsservice.entity.QPostEntity;
import pl.dev.news.devnewsservice.repository.custom.CustomQueryDslRspository;

import java.util.UUID;

@Repository
public interface PostRepository extends CustomQueryDslRspository<PostEntity, QPostEntity, UUID> {

}
