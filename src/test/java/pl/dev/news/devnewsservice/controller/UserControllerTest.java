package pl.dev.news.devnewsservice.controller;


import org.junit.Assert;
import org.junit.Test;
import pl.dev.news.devnewsservice.AbstractIntegrationTest;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.utils.PathUtils;
import pl.dev.news.model.rest.RestTokenResponse;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.dev.news.controller.api.UserApi.deleteUserPath;
import static pl.dev.news.controller.api.UserApi.getUserPath;
import static pl.dev.news.controller.api.UserApi.getUsersPath;
import static pl.dev.news.devnewsservice.entity.UserRoleEntity.USER;

public class UserControllerTest extends AbstractIntegrationTest {

    @Test
    public void testDeleteUser() throws Exception {
        final UserEntity userEntity = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(userEntity);
        mockMvc.perform(
            delete(PathUtils.generate(deleteUserPath, userEntity.getId()))
                    .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                    .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent());
        final UserEntity userEntityFromDb = getUser(userEntity.getId());
        Assert.assertNotNull(userEntityFromDb.getDeletedAt());
    }

    @Test
    public void testDeleteUserInvalidToken() throws Exception {
        final UserEntity userEntity = createUser(USER);
        final String accessToken = "invalid";
        mockMvc.perform(
                delete(PathUtils.generate(deleteUserPath, userEntity.getId()))
                        .header(AUTHORIZATION, accessToken)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetUser() throws Exception {
        final UserEntity expected = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(expected);
        mockMvc.perform(
                get(PathUtils.generate(getUserPath, expected.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + expected.getId() + "')]").exists());
    }

    @Test
    public void testGetUsers() throws Exception {
        final UserEntity expected = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(expected);
        mockMvc.perform(
                get(PathUtils.generate(getUsersPath))
                        .param("username", expected.getUsername())
                        .param("name", expected.getFirstName())
                        .param("email", expected.getEmail())
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",  hasSize(1)))
                .andExpect(jsonPath("$[?(@.id == '" + expected.getId() + "')]").exists());
    }
}
