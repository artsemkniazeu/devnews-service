package pl.dev.news.devnewsservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.dev.news.devnewsservice.entity.GroupEntity;
import pl.dev.news.devnewsservice.entity.QGroupEntity;
import pl.dev.news.devnewsservice.repository.custom.CustomQueryDslRspository;

import java.util.List;
import java.util.UUID;

public interface GroupRepository extends CustomQueryDslRspository<GroupEntity, QGroupEntity, UUID> {

    // Bug (order and type cast)
    // https://github.com/spring-projects/spring-framework/issues/22450
    // https://stackoverflow.com/questions/59648241/hibernate-error-jpa-query-spel-expression-org-hibernate-queryexception-nam

    @Query(value = "select g.* "
            + "from groups g "
            + "    left join group_user gu "
            + "        on g.id = gu.group_id  "
            + "               and (:groupId is null or g.id = uuid(cast(:groupId as text))) "
            + "where (:userId is null or gu.user_id = uuid(cast(:userId as text))) "
            + "  and (:ownerId is null or g.owner_id = uuid(cast(:ownerId as text))) "
            + "  and ((:name is null or :name = '' or g.name like '%'||cast(:name as citext)||'%') "
            + "    or (:value is null or :value = '' or g.value like '%'||cast(:value as citext)||'%')) "
            + "  and g.deleted_at is null",
            countQuery = "select count(g.id) "
                    + "from groups g "
                    + "    left join group_user gu "
                    + "        on g.id = gu.group_id  "
                    + "               and (:groupId is null or g.id = uuid(cast(:groupId as text))) "
                    + "where (:userId is null or gu.user_id = uuid(cast(:userId as text))) "
                    + "  and (:ownerId is null or g.owner_id = uuid(cast(:ownerId as text))) "
                    + "  and ((:name is null or :name = '' or g.name like '%'||cast(:name as citext)||'%') "
                    + "    or (:value is null or :value = '' or g.value like '%'||cast(:value as citext)||'%')) "
                    + "  and g.deleted_at is null",
            nativeQuery = true)
    Page<GroupEntity> find(
            @Param("groupId") UUID groupId, 
            @Param("userId") UUID userId,
            @Param("ownerId") UUID ownerId,
            @Param("name") String name,
            @Param("value") String value,
            Pageable pageable
    );

    @Modifying
    @Query(value = "delete from group_user gu "
            + "using groups g "
            + "where gu.group_id = g.id "
            + "    and gu.group_id in (:ids) "
            + "    and gu.user_id = uuid(cast(:userId as text))",
            nativeQuery = true)
    void unFollowAll(@Param("ids") List<UUID> ids, @Param("userId") UUID id);
}
