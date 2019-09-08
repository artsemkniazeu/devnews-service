package pl.dev.news.devnewsservice.repository;

import pl.dev.news.devnewsservice.entity.GroupEntity;
import pl.dev.news.devnewsservice.entity.QGroupEntity;
import pl.dev.news.devnewsservice.repository.custom.CustomQueryDslRspository;

import java.util.UUID;

public interface GroupRepository extends CustomQueryDslRspository<GroupEntity, QGroupEntity, UUID> {

}
