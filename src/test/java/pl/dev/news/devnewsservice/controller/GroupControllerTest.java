package pl.dev.news.devnewsservice.controller;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import pl.dev.news.devnewsservice.AbstractIntegrationTest;
import pl.dev.news.devnewsservice.entity.GroupEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.utils.PathUtils;
import pl.dev.news.devnewsservice.utils.TestUtils;
import pl.dev.news.model.rest.RestGroupModel;
import pl.dev.news.model.rest.RestIdModel;
import pl.dev.news.model.rest.RestTokenResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.dev.news.controller.api.GroupApi.createPath;
import static pl.dev.news.controller.api.GroupApi.deletePath;
import static pl.dev.news.controller.api.GroupApi.findPath;
import static pl.dev.news.controller.api.GroupApi.followPath;
import static pl.dev.news.controller.api.GroupApi.retrievePath;
import static pl.dev.news.controller.api.GroupApi.unfollowMultiplePath;
import static pl.dev.news.controller.api.GroupApi.unfollowPath;
import static pl.dev.news.controller.api.GroupApi.updatePath;
import static pl.dev.news.controller.api.GroupApi.uploadGroupBackgroundPath;
import static pl.dev.news.controller.api.GroupApi.uploadGroupImagePath;
import static pl.dev.news.devnewsservice.entity.UserRoleEntity.USER;

public class GroupControllerTest extends AbstractIntegrationTest {

    @Test
    public void testCreateGroup() throws Exception {
        // given
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenModel = tokenProvider.createTokenModel(user);
        final RestGroupModel model = TestUtils.restGroupModel(user.getId());
        // when
        mockMvc.perform(
                post(createPath)
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, tokenModel.getAccess().getToken())
                        .content(objectMapper.writeValueAsBytes(model)))
                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void testDeleteGroup() throws Exception {
        // given
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenModel = tokenProvider.createTokenModel(user);
        final GroupEntity entity = createGroup(user);
        // when
        mockMvc.perform(
                delete(deletePath, entity.getId())
                        .header(AUTHORIZATION, tokenModel.getAccess().getToken()))
                // then
                .andExpect(status().isNoContent());

        final GroupEntity entityFromDB = getGroup(entity.getId());
        Assert.assertNotNull(entityFromDB.getDeletedAt());
    }

    @Test
    public void testDeleteGroupNotFound() throws Exception {
        // given
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenModel = tokenProvider.createTokenModel(user);
        // when
        mockMvc.perform(
                delete(deletePath, UUID.randomUUID())
                        .header(AUTHORIZATION, tokenModel.getAccess().getToken()))
                // then
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetGroups() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        final GroupEntity entity = createGroup(user);
        mockMvc.perform(
                get(PathUtils.generate(findPath))
                        .param("name", entity.getName())
                        .param("value", entity.getValue())
                        .param("ownerId", entity.getOwnerId().toString())
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",  hasSize(1)))
                .andExpect(jsonPath("$[?(@.id == '" + entity.getId() + "')]").exists());
    }

    @Test
    public void testGetGroup() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        final GroupEntity entity = createGroup(user);
        mockMvc.perform(
                get(PathUtils.generate(retrievePath, entity.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + entity.getId() + "')]").exists());
    }

    @Test
    public void testGetGroupNotFound() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        mockMvc.perform(
                get(PathUtils.generate(retrievePath, UUID.randomUUID()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateGroup() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        final RestGroupModel model = TestUtils.restGroupModel(user.getId());
        final GroupEntity entity = createGroup(user, model);
        model.setName("changed");
        mockMvc.perform(
                put(PathUtils.generate(updatePath, entity.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .content(objectMapper.writeValueAsBytes(model))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + entity.getId() + "')]").exists())
                .andExpect(jsonPath("$[?(@.name == '" + model.getName() + "')]").exists());
    }

    @Test
    public void testUpdateGroupNotFound() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        final RestGroupModel model = TestUtils.restGroupModel(user.getId());
        mockMvc.perform(
                put(PathUtils.generate(updatePath, UUID.randomUUID()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .content(objectMapper.writeValueAsBytes(model))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFollowGroup() throws Exception {
        final UserEntity user = createUser(USER);
        final UserEntity creator = createUser(USER);
        final GroupEntity group = createGroup(creator);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        mockMvc.perform(
                post(PathUtils.generate(followPath, group.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testFollowGroupNotFound() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        mockMvc.perform(
                post(PathUtils.generate(followPath, UUID.randomUUID()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUnFollowGroup() throws Exception {
        final UserEntity user = createUser(USER);
        final UserEntity creator = createUser(USER);
        final GroupEntity group = createGroup(creator);
        followGroup(user, group);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        mockMvc.perform(
                delete(PathUtils.generate(unfollowPath, group.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUnFollowGroupNotFound() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        mockMvc.perform(
                delete(PathUtils.generate(unfollowPath, UUID.randomUUID()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUnFollowGroupMultiple() throws Exception {
        final UserEntity user = createUser(USER);
        final UserEntity creator = createUser(USER);
        final GroupEntity group = createGroup(creator);
        final GroupEntity group2 = createGroup(creator);
        followGroup(user, group);
        followGroup(user, group2);
        final List<RestIdModel> ids = new ArrayList<>();
        ids.add(new RestIdModel().id(group.getId()));
        ids.add(new RestIdModel().id(group2.getId()));
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        mockMvc.perform(
                post(PathUtils.generate(unfollowMultiplePath))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .content(objectMapper.writeValueAsBytes(ids))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUploadGroupImage() throws Exception {
        // given
        final UserEntity user = createUser(USER);
        final GroupEntity group = createGroup(user);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        final MockMultipartFile file = TestUtils.getMultipartFile("avatar.jpg", "image/jpeg");

        final String url = "https://example.com/image.jpg";
        mockBucketUpload(url);
        // when
        mockMvc.perform(
                multipart(PathUtils.generate(uploadGroupImagePath, group.getId()))
                        .file(file)
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken()))
                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[?(@.id)]").exists())
                .andExpect(jsonPath("$[?(@.url == '" + url + "')]").exists());

        final GroupEntity groupFromDb = getGroup(group.getId());
        Assert.assertEquals(groupFromDb.getImageUrl(), url);
    }


    @Test
    public void testUploadGroupBackgroundImage() throws Exception {
        // given
        final UserEntity user = createUser(USER);
        final GroupEntity group = createGroup(user);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        final MockMultipartFile file = TestUtils.getMultipartFile("avatar.jpg", "image/jpeg");

        final String url = "https://example.com/image.jpg";
        mockBucketUpload(url);
        // when
        mockMvc.perform(
                multipart(PathUtils.generate(uploadGroupBackgroundPath, group.getId()))
                        .file(file)
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken()))
                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[?(@.id)]").exists())
                .andExpect(jsonPath("$[?(@.url == '" + url + "')]").exists());

        final GroupEntity groupFromDb = getGroup(group.getId());
        Assert.assertEquals(groupFromDb.getBgUrl(), url);
    }
}
