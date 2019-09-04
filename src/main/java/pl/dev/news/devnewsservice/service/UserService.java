package pl.dev.news.devnewsservice.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import pl.dev.news.model.rest.RestUploadModel;
import pl.dev.news.model.rest.RestUserModel;
import pl.dev.news.model.rest.RestUserQueryParameters;

import java.util.UUID;

public interface UserService {

    void delete(UUID userId);

    RestUserModel get(UUID userId);

    Page<RestUserModel> getUsers(RestUserQueryParameters parameters, Integer page, Integer size);

    RestUserModel update(UUID userId, RestUserModel restUserModel);

    RestUploadModel uploadImage(UUID userId, MultipartFile file);
}
