package pl.dev.news.devnewsservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.dev.news.devnewsservice.entity.QUserEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.repository.custom.CustomQueryDslRspository;

import java.util.UUID;

@Repository
public interface UserRepository extends CustomQueryDslRspository<UserEntity, QUserEntity, UUID> {

    @Query(value = "select u.* "
            + "from users u "
            + "where (:email is null or :email  = '' or u.email = :email) "
            + "    and (:email is not null or :email != '' or ( "
            + "        (:name is null or :name = '' or u.full_name like concat('%', :name, '%')) "
            + "        or (:username is null or :username = '' or u.username like concat('%', :username, '%')) "
            + "    )) "
            + "    and u.deleted_at is null",
            countQuery = "select count(u.id) "
                    + "from users u "
                    + "where (:email is null or :email  = '' or u.email = :email) "
                    + "    and (:email is not null or :email != '' or ( "
                    + "        (:name is null or :name = '' or u.full_name like concat('%', :name, '%')) "
                    + "        or (:username is null or :username = '' or u.username like concat('%', :username, '%')) "
                    + "    )) "
                    + "    and u.deleted_at is null",
            nativeQuery = true)
    Page<UserEntity> findAll(
            @Param("email") String email,
            @Param("name") String name,
            @Param("username") String username,
            Pageable pageable
    );
    
    @Query(value = "select u.* "
            + "from users u "
            + "    join user_follower uf "
            + "    on u.id = uf.user_id and uf.follower_id = :userId "
            + "where (:id is null or :id = '' or u.id = uuid(:id)) "
            + "    and (:email is null or :email  = '' or u.email = :email) "
            + "    and (:email is not null or :email != '' or ( "
            + "        (:name is null or :name = '' or u.full_name like concat('%', :name, '%')) "
            + "        or (:username is null or :username = '' or u.username like concat('%', :username, '%')) "
            + "    )) "
            + "    and u.deleted_at is null",
            countQuery = "select count(u.id) "
                    + "from users u "
                    + "    join user_follower uf "
                    + "    on u.id = uf.user_id and uf.follower_id = :userId "
                    + "where (:id is null or :id = '' or u.id = uuid(:id)) "
                    + "    and (:email is null or :email  = '' or u.email = :email) "
                    + "    and (:email is not null or :email != '' or ( "
                    + "        (:name is null or :name = '' or u.full_name like concat('%', :name, '%')) "
                    + "        or (:username is null or :username = '' or u.username like concat('%', :username, '%')) "
                    + "    )) "
                    + "    and u.deleted_at is null",
            nativeQuery = true)
    Page<UserEntity> findAllFollowing(
            @Param("userId") UUID userId,
            @Param("id") String id,
            @Param("email") String email,
            @Param("name") String name,
            @Param("username") String username,
            Pageable pageable
    );


    @Query(value = "select u.* "
            + "from users u "
            + "    join user_follower uf "
            + "    on u.id = uf.follower_id and uf.user_id = :userId "
            + "where (:id is null or :id = '' or uf.follower_id = uuid(:id)) "
            + "    and (:email is null or :email  = '' or u.email = :email) "
            + "    and (:email is not null or :email != '' or ( "
            + "        (:name is null or :name = '' or u.full_name like concat('%', :name, '%')) "
            + "        or (:username is null or :username = '' or u.username like concat('%', :username, '%')) "
            + "    )) "
            + "    and u.deleted_at is null",
            countQuery = "select count(u.id) "
                    + "from users u "
                    + "    join user_follower uf "
                    + "    on u.id = uf.follower_id and uf.user_id = :userId "
                    + "where (:id is null or :id = '' or uf.follower_id = uuid(:id)) "
                    + "    and (:email is null or :email  = '' or u.email = :email) "
                    + "    and (:email is not null or :email != '' or ( "
                    + "        (:name is null or :name = '' or u.full_name like concat('%', :name, '%')) "
                    + "        or (:username is null or :username = '' or u.username like concat('%', :username, '%')) "
                    + "    )) "
                    + "    and u.deleted_at is null",
            nativeQuery = true)
    Page<UserEntity> findAllFollowers(
            @Param("userId") UUID userId,
            @Param("id") String id,
            @Param("email") String email,
            @Param("name") String name,
            @Param("username") String username,
            Pageable pageable
    );

    
}
