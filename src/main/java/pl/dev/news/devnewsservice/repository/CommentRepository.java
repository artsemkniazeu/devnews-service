package pl.dev.news.devnewsservice.repository;

import org.springframework.stereotype.Repository;
import pl.dev.news.devnewsservice.entity.CommentEntity;
import pl.dev.news.devnewsservice.entity.QCommentEntity;
import pl.dev.news.devnewsservice.repository.custom.CustomQueryDslRspository;

import java.util.UUID;

@Repository
public interface CommentRepository extends CustomQueryDslRspository<CommentEntity, QCommentEntity, UUID> {

}
