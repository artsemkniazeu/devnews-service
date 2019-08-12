package pl.dev.news.devnewsservice.controller;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import pl.dev.news.devnewsservice.AbstractIntegrationTest;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.utils.PathUtils;
import pl.dev.news.model.rest.RestTokenModel;
import pl.dev.news.model.rest.RestUserModel;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.dev.news.controller.api.UserApi.deleteUserPath;
import static pl.dev.news.controller.api.UserApi.getUserPath;
import static pl.dev.news.controller.api.UserApi.getUsersPath;

public class UserControllerTest extends AbstractIntegrationTest {

    @Test
    public void testDeleteUser() throws Exception {
        final UserEntity userEntity = createUser();
        final RestTokenModel tokenModel = tokenProvider.createTokenModel(userEntity);
        mockMvc.perform(
            delete(PathUtils.generate(deleteUserPath, userEntity.getId()))
                    .header(AUTHORIZATION, tokenModel.getAccessToken())
                    .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent());
        final UserEntity userEntityFromDb = getUser(userEntity.getId());
        Assert.assertNotNull(userEntityFromDb.getDeletedAt());
    }

    @Test
    public void testDeleteUserInvalidToken() throws Exception {
        final UserEntity userEntity = createUser();
        final String accessToken = "invalid";
        mockMvc.perform(
                delete(PathUtils.generate(deleteUserPath, userEntity.getId()))
                        .header(AUTHORIZATION, accessToken)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetUser() throws Exception {
        final UserEntity expected = createUser();
        final RestTokenModel tokenModel = tokenProvider.createTokenModel(expected);
        final MvcResult response =  mockMvc.perform(
                get(PathUtils.generate(getUserPath, expected.getId()))
                        .header(AUTHORIZATION, tokenModel.getAccessToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final RestUserModel result = objectMapper.readValue(
                response.getResponse().getContentAsString(), RestUserModel.class
        );

        assertEntityAndModel(expected, result);
    }

    @Test
    public void testGetUsers() throws Exception {
        final UserEntity expected = createUser();
        final RestTokenModel tokenModel = tokenProvider.createTokenModel(expected);
        final MvcResult response =  mockMvc.perform(
                get(PathUtils.generate(getUsersPath))
                        .header(AUTHORIZATION, tokenModel.getAccessToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final List<RestUserModel> result = objectMapper.readValue(
                response.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, RestUserModel.class)
        );
        Assert.assertEquals(1, result.size());
        assertEntityAndModel(expected, result.get(0));
    }

    private void assertEntityAndModel(final UserEntity entity, final RestUserModel model) {
        Assert.assertEquals(entity.getEmail(), model.getEmail());
        Assert.assertEquals(entity.getFirstName(), model.getFirstName());
        Assert.assertEquals(entity.getLastName(), model.getLastName());
    }

}
