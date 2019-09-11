package pl.dev.news.devnewsservice.repository;

import org.springframework.stereotype.Repository;
import pl.dev.news.devnewsservice.entity.QUserEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.repository.custom.CustomQueryDslRspository;

import java.util.UUID;

@Repository
public interface UserRepository extends CustomQueryDslRspository<UserEntity, QUserEntity, UUID> {

}
