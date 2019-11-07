package pl.dev.news.devnewsservice.repository;

import org.springframework.stereotype.Repository;
import pl.dev.news.devnewsservice.entity.QUserEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.repository.custom.CustomQueryDslRspository;

import java.util.UUID;

@Repository
public interface UserRepository extends CustomQueryDslRspository<UserEntity, QUserEntity, UUID> {

    //@Query(value = "select u.*\n"
    //        + "from users u join user_fullname as uf on u.id = uf.id\n"
    //        + "where (:name = '' or u.first_name like concat('%', :name, '%'))\n"
    //        + "   or (:name = '' or u.last_name like concat('%', :name, '%'))\n"
    //        + "   or (:name = '' or uf.fullname like concat('%', :name, '%'))\n"
    //        + "   or (:username = '' or u.username like concat('%', :username, '%'))\n"
    //        + "   or (:email = '' or u.email = :email)\n"
    //        + "    AND u.deleted_at IS NULL",
    //        countQuery = "select count(u.id) "
    //                + "from users u "
    //                + "where (:name = '' or u.first_name like concat('%', :name, '%')) "
    //                + "    or (:name = '' or u.last_name like concat('%', :name, '%')) "
    //                + "    or (:username = '' or u.username like concat('%', :username, '%')) "
    //                + "    or (:email = '' or u.email = :email)"
    //                + "   AND u.deleted_at IS NULL ",
    //        nativeQuery = true
    //)
    //Page<UserEntity> findAll(
    //        @Param("name") String name,
    //        @Param("username") String username,
    //        @Param("email") String email,
    //        Pageable pageable
    //);

}
