package pl.dev.news.devnewsservice.controller;

import org.junit.Assert;
import org.junit.Test;
import pl.dev.news.devnewsservice.AbstractIntegrationTest;
import pl.dev.news.devnewsservice.entity.CommentEntity;
import pl.dev.news.devnewsservice.entity.PostEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.utils.PathUtils;
import pl.dev.news.devnewsservice.utils.TestUtils;
import pl.dev.news.model.rest.RestCommentModel;
import pl.dev.news.model.rest.RestTokenResponse;

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
import static pl.dev.news.controller.api.CommentApi.createCommentPath;
import static pl.dev.news.controller.api.CommentApi.deleteCommentPath;
import static pl.dev.news.controller.api.CommentApi.getCommentPath;
import static pl.dev.news.controller.api.CommentApi.getCommentsPath;
import static pl.dev.news.controller.api.CommentApi.updateCommentPath;
import static pl.dev.news.devnewsservice.entity.UserRoleEntity.USER;

public class CommentControllerTest extends AbstractIntegrationTest {

    @Test
    public void testCreateComment() throws Exception {
        // given
        final UserEntity user = createUser(USER);
        final PostEntity post = createPost(user);
        final CommentEntity parent = createComment(user);
        final RestTokenResponse tokenModel = tokenProvider.createTokenModel(user);
        final RestCommentModel model = TestUtils.restCommentModel(post.getId());
        model.setParentId(parent.getId());
        // when
        mockMvc.perform(
                post(createCommentPath)
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, tokenModel.getAccess().getToken())
                        .content(objectMapper.writeValueAsBytes(model)))
                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void testCreateCommentPostNotFound() throws Exception {
        // given
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenModel = tokenProvider.createTokenModel(user);
        final RestCommentModel model = TestUtils.restCommentModel(UUID.randomUUID());
        // when
        mockMvc.perform(
                post(createCommentPath)
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, tokenModel.getAccess().getToken())
                        .content(objectMapper.writeValueAsBytes(model)))
                // then
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateCommentParentNotFound() throws Exception {
        // given
        final UserEntity user = createUser(USER);
        final PostEntity post = createPost(user);
        final RestTokenResponse tokenModel = tokenProvider.createTokenModel(user);
        final RestCommentModel model = TestUtils.restCommentModel(post.getId());
        model.setParentId(UUID.randomUUID());
        // when
        mockMvc.perform(
                post(createCommentPath)
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, tokenModel.getAccess().getToken())
                        .content(objectMapper.writeValueAsBytes(model)))
                // then
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteComment() throws Exception {
        // given
        final UserEntity user = createUser(USER);
        final CommentEntity comment = createComment(user);
        final RestTokenResponse tokenModel = tokenProvider.createTokenModel(user);
        // when
        mockMvc.perform(
                delete(deleteCommentPath, comment.getId())
                        .header(AUTHORIZATION, tokenModel.getAccess().getToken()))
                // then
                .andExpect(status().isNoContent());

        final CommentEntity commentFromDB = getComment(comment.getId());
        Assert.assertNotNull(commentFromDB.getDeletedAt());
    }

    @Test
    public void testDeleteGroupNotFound() throws Exception {
        // given
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenModel = tokenProvider.createTokenModel(user);
        // when
        mockMvc.perform(
                delete(deleteCommentPath, UUID.randomUUID())
                        .header(AUTHORIZATION, tokenModel.getAccess().getToken()))
                // then
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateComment() throws Exception {
        final UserEntity user = createUser(USER);
        final PostEntity post = createPost(user);
        final RestCommentModel model = TestUtils.restCommentModel(post.getId());
        final CommentEntity comment = createComment(user, post, model);

        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        model.setText("changed");
        mockMvc.perform(
                put(PathUtils.generate(updateCommentPath, comment.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .content(objectMapper.writeValueAsBytes(model))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + comment.getId() + "')]").exists())
                .andExpect(jsonPath("$[?(@.text == '" + model.getText() + "')]").exists());
    }

    @Test
    public void testUpdateCommentNotFound() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        final RestCommentModel model = TestUtils.restCommentModel(user.getId());
        mockMvc.perform(
                put(PathUtils.generate(updateCommentPath, UUID.randomUUID()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .content(objectMapper.writeValueAsBytes(model))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetComment() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        final CommentEntity comment = createComment(user);
        mockMvc.perform(
                get(PathUtils.generate(getCommentPath, comment.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + comment.getId() + "')]").exists());
    }

    @Test
    public void testGetCommentNotFound() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        mockMvc.perform(
                get(PathUtils.generate(getCommentPath, UUID.randomUUID()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetComments() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        final CommentEntity entity = createComment(user);
        mockMvc.perform(
                get(PathUtils.generate(getCommentsPath))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",  hasSize(1)))
                .andExpect(jsonPath("$[?(@.id == '" + entity.getId() + "')]").exists());
    }

}
