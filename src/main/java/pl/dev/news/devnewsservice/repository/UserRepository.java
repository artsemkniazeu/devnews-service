package pl.dev.news.devnewsservice.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.repository.custom.SoftJpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends SoftJpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findOneByEmail(String email);

    @Query(value = "select u.* from users u "
            + "where (:username = '' or u.username = :username) "
            + "and (:email = '' or u.email = :email) "
            + "and (:name = '' or u.first_name like concat('%', :name, '%')) "
            + "or (:name = '' or u.last_name like concat('%', :name, '%')) "
            + "or (:name = '' or concat(u.first_name, ' ', u.last_name) like concat('%', :name, '%')) "
            + "or (:name = '' or concat(u.last_name, ' ', u.first_name) like concat('%', :name, '%')) ",
            countQuery = "select count(u.id) from users u "
                    + "where (:username = '' or u.username = :username) "
                    + "and (:email = '' or u.email = :email) "
                    + "and (:name = '' or u.first_name like concat('%', :name, '%')) "
                    + "or (:name = '' or u.last_name like concat('%', :name, '%')) "
                    + "or (:name = '' or concat(u.first_name, ' ', u.last_name) like concat('%', :name, '%')) "
                    + "or (:name = '' or concat(u.last_name, ' ', u.first_name) like concat('%', :name, '%')) ",
            nativeQuery = true)
    Page<UserEntity> findAll(
            @Param("username") String username,
            @Param("name") String name,
            @Param("email") String email,
            Pageable pageable
    );

}
