package pl.dev.news.devnewsservice.repository;

import pl.dev.news.devnewsservice.entity.QUserEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.repository.custom.CustomQueryDslRspository;

import java.util.UUID;

public interface UserRepository extends CustomQueryDslRspository<UserEntity, QUserEntity, UUID> {


}
