package pl.dev.news.devnewsservice.controller;

import org.junit.Assert;
import org.junit.Test;
import pl.dev.news.devnewsservice.AbstractIntegrationTest;
import pl.dev.news.devnewsservice.entity.GroupEntity;
import pl.dev.news.devnewsservice.entity.PostEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.utils.PathUtils;
import pl.dev.news.devnewsservice.utils.TestUtils;
import pl.dev.news.model.rest.RestPostModel;
import pl.dev.news.model.rest.RestTokenResponse;

import java.util.ArrayList;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.dev.news.controller.api.PostApi.createPostPath;
import static pl.dev.news.controller.api.PostApi.deletePostPath;
import static pl.dev.news.controller.api.PostApi.getPostPath;
import static pl.dev.news.controller.api.PostApi.getPostsPath;
import static pl.dev.news.controller.api.PostApi.updatePostPath;
import static pl.dev.news.devnewsservice.entity.UserRoleEntity.USER;

public class PostControllerTest extends AbstractIntegrationTest {

    @Test
    public void testCreatePost() throws Exception {
        // given
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenModel = tokenProvider.createTokenModel(user);
        final RestPostModel model = TestUtils
                .restPostModel(createTags(), createCategory().getChildren(), createGroup(user));
        // when
        mockMvc.perform(
                post(createPostPath)
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, tokenModel.getAccess().getToken())
                        .content(objectMapper.writeValueAsBytes(model)))
                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void testDeletePost() throws Exception {
        // given
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenModel = tokenProvider.createTokenModel(user);
        final PostEntity entity = createPost(user);
        // when
        mockMvc.perform(
                delete(deletePostPath, entity.getId())
                .header(AUTHORIZATION, tokenModel.getAccess().getToken()))
                // then
                .andExpect(status().isNoContent());

        final PostEntity entityFromDB = getPost(entity.getId());
        Assert.assertNotNull(entityFromDB.getDeletedAt());
    }

    @Test
    public void testDeletePostNotFound() throws Exception {
        // given
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenModel = tokenProvider.createTokenModel(user);
        // when
        mockMvc.perform(
                delete(deletePostPath, UUID.randomUUID())
                        .header(AUTHORIZATION, tokenModel.getAccess().getToken()))
                // then
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetPosts() throws Exception {
        final UserEntity user = createUser(USER);
        final UserEntity publisher = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        final PostEntity entity = createPost(publisher);
        final UUID tagId = new ArrayList<>(entity.getTags()).get(0).getId();
        final UUID categoryId = new ArrayList<>(entity.getCategories()).get(0).getId();
        mockMvc.perform(
                get(PathUtils.generate(getPostsPath))
                        .param("title", entity.getTitle())
                        .param("text", entity.getText().substring(0, 2))
                        .param("tagId", tagId.toString())
                        .param("categoryId", categoryId.toString())
                        //.param("groupId", entity.getGroupId().toString()) // TODO create group
                        .param("publisherId", publisher.getId().toString())
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",  hasSize(1)))
                .andExpect(jsonPath("$[?(@.id == '" + entity.getId() + "')]").exists());
    }

    @Test
    public void testGetPost() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        final PostEntity entity = createPost(user);
        mockMvc.perform(
                get(PathUtils.generate(getPostPath, entity.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + entity.getId() + "')]").exists());
    }

    @Test
    public void testGetPostNotFound() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        mockMvc.perform(
                get(PathUtils.generate(getPostPath, UUID.randomUUID()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    public void testUpdatePost() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        final GroupEntity group = createGroup(user);
        final RestPostModel model = TestUtils
                .restPostModel(createTags(), createCategory().getChildren(), group);
        final PostEntity entity = createPost(user, model, group);
        model.getTags().remove(0);
        model.getTags().addAll(TestUtils.restTagModels());
        model.setTitle("changed");
        mockMvc.perform(
                put(PathUtils.generate(updatePostPath, entity.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .content(objectMapper.writeValueAsBytes(model))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + entity.getId() + "')]").exists())
                .andExpect(jsonPath("$[?(@.title == '" + model.getTitle() + "')]").exists())
                .andExpect(jsonPath("$.tags", hasSize(9)));
    }

    @Test
    public void testUpdatePostNotFound() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        final GroupEntity group = createGroup(user);
        final RestPostModel model = TestUtils
                .restPostModel(createTags(), createCategory().getChildren(), group);
        mockMvc.perform(
                put(PathUtils.generate(updatePostPath, UUID.randomUUID()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .content(objectMapper.writeValueAsBytes(model))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
