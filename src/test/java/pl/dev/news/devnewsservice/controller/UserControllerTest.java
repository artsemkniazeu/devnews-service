package pl.dev.news.devnewsservice.controller;


import org.junit.Assert;
import org.junit.Test;
import pl.dev.news.devnewsservice.AbstractIntegrationTest;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.utils.PathUtils;
import pl.dev.news.model.rest.RestTokenModel;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.dev.news.controllers.api.UserApi.deleteUserPath;

public class UserControllerTest extends AbstractIntegrationTest {

    @Test
    public void testDeleteUser() throws Exception {
        final UserEntity userEntity = createUser();
        final RestTokenModel tokenModel = tokenProvider.createTokenModel(userEntity);
        mockMvc.perform(
            delete(PathUtils.generate(deleteUserPath, userEntity.getId()))
                    .header(AUTHORIZATION, tokenModel.getAccessToken())
                    .contentType(APPLICATION_JSON)
        ).andExpect(status().isNoContent());
        final UserEntity userEntityFromDb = getUser(userEntity.getId());
        Assert.assertNotNull(userEntityFromDb.getDeletedAt());
    }

}
