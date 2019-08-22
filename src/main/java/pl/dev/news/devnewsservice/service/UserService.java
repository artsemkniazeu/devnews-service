package pl.dev.news.devnewsservice.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import pl.dev.news.model.rest.RestUploadModel;
import pl.dev.news.model.rest.RestUserModel;

import java.util.UUID;

public interface UserService {

    void delete(UUID userId);

    RestUserModel get(UUID userId);

    Page<RestUserModel> getUsers(String username, String name, String email, Integer page, Integer size);

    RestUserModel update(UUID userId, RestUserModel restUserModel);

    RestUploadModel uploadImage(UUID userId, MultipartFile file);
}
