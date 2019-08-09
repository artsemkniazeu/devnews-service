package pl.dev.news.devnewsservice.service;

import pl.dev.news.model.rest.RestUserModel;

import java.util.UUID;

public interface UserService {

    void delete(UUID userId);

    RestUserModel get(UUID userId);

}
