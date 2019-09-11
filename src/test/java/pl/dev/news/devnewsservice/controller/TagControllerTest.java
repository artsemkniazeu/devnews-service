package pl.dev.news.devnewsservice.controller;

import org.junit.Assert;
import org.junit.Test;
import pl.dev.news.devnewsservice.AbstractIntegrationTest;
import pl.dev.news.devnewsservice.entity.TagEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.utils.PathUtils;
import pl.dev.news.devnewsservice.utils.TestUtils;
import pl.dev.news.model.rest.RestTagModel;
import pl.dev.news.model.rest.RestTokenResponse;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.dev.news.controller.api.TagApi.createTagPath;
import static pl.dev.news.controller.api.TagApi.deleteTagPath;
import static pl.dev.news.controller.api.TagApi.getTagPath;
import static pl.dev.news.controller.api.TagApi.getTagsPath;
import static pl.dev.news.devnewsservice.entity.UserRoleEntity.USER;

public class TagControllerTest extends AbstractIntegrationTest {

    @Test
    public void testCreateTag() throws Exception {
        // given
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenModel = tokenProvider.createTokenModel(user);
        final RestTagModel model = TestUtils.restTagModel();
        // when
        mockMvc.perform(
                post(createTagPath)
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, tokenModel.getAccess().getToken())
                        .content(objectMapper.writeValueAsBytes(model)))
                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void testDeleteTag() throws Exception {
        // given
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenModel = tokenProvider.createTokenModel(user);
        final TagEntity entity = createTag();
        // when
        mockMvc.perform(delete(deleteTagPath, entity.getId())
                .header(AUTHORIZATION, tokenModel.getAccess().getToken()))
                // then
                .andExpect(status().isNoContent());

        final TagEntity entityFromDB = getTag(entity.getId());
        Assert.assertNotNull(entityFromDB.getDeletedAt());
    }

    @Test
    public void testGetTag() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        final TagEntity entity = createTag();
        mockMvc.perform(
                get(PathUtils.generate(getTagPath, entity.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + entity.getId() + "')]").exists());
    }

    @Test
    public void testGetTags() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        final TagEntity entity = createTag();
        mockMvc.perform(
                get(PathUtils.generate(getTagsPath))
                        .param("name", entity.getName())
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",  hasSize(1)))
                .andExpect(jsonPath("$[?(@.id == '" + entity.getId() + "')]").exists());
    }

    @Test
    public void testUpdateTag() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        final RestTagModel model = TestUtils.restTagModel();
        final TagEntity entity = createTag(model);
        model.setName("changed");
        mockMvc.perform(
                put(PathUtils.generate(getTagPath, entity.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .content(objectMapper.writeValueAsBytes(model))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + entity.getId() + "')]").exists())
                .andExpect(jsonPath("$[?(@.name == '" + model.getName() + "')]").exists());
    }
}
