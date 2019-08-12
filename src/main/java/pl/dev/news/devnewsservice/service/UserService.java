package pl.dev.news.devnewsservice.service;

import org.springframework.data.domain.Page;
import pl.dev.news.model.rest.RestUserModel;

import java.util.UUID;

public interface UserService {

    void delete(UUID userId);

    RestUserModel get(UUID userId);

    Page<RestUserModel> getUsers(String username, String name, String email, Integer page, Integer size);
}
