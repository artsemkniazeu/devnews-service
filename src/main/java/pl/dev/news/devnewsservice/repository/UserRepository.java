package pl.dev.news.devnewsservice.repository;


import org.springframework.stereotype.Repository;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.repository.custom.SoftJpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends SoftJpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findOneByEmail(String email);

}
