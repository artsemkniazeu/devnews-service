package pl.dev.news.devnewsservice.service;

import org.springframework.web.multipart.MultipartFile;
import pl.dev.news.devnewsservice.entity.UploadEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;

public interface UploadService {

    UploadEntity upload(UserEntity entity, MultipartFile file, String bucket);

    void delete(UploadEntity entity);

}
