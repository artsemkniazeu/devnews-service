package pl.dev.news.devnewsservice.repository;

import pl.dev.news.devnewsservice.entity.QUploadEntity;
import pl.dev.news.devnewsservice.entity.UploadEntity;
import pl.dev.news.devnewsservice.repository.custom.CustomQueryDslRspository;

import java.util.UUID;

public interface UploadRepository extends CustomQueryDslRspository<UploadEntity, QUploadEntity, UUID> {

}
