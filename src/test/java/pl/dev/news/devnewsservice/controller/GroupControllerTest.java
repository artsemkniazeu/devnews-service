package pl.dev.news.devnewsservice.controller;

import org.junit.Assert;
import org.junit.Test;
import pl.dev.news.devnewsservice.AbstractIntegrationTest;
import pl.dev.news.devnewsservice.entity.GroupEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.utils.PathUtils;
import pl.dev.news.devnewsservice.utils.TestUtils;
import pl.dev.news.model.rest.RestGroupModel;
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
import static pl.dev.news.controller.api.GroupApi.createGroupPath;
import static pl.dev.news.controller.api.GroupApi.deleteGroupPath;
import static pl.dev.news.controller.api.GroupApi.getGroupPath;
import static pl.dev.news.controller.api.GroupApi.getGroupsPath;
import static pl.dev.news.controller.api.GroupApi.updateGroupPath;
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
                post(createGroupPath)
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
                delete(deleteGroupPath, entity.getId())
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
                delete(deleteGroupPath, UUID.randomUUID())
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
                get(PathUtils.generate(getGroupsPath))
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
                get(PathUtils.generate(getGroupPath, entity.getId()))
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
                get(PathUtils.generate(getGroupPath, UUID.randomUUID()))
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
                put(PathUtils.generate(updateGroupPath, entity.getId()))
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
                put(PathUtils.generate(updateGroupPath, UUID.randomUUID()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .content(objectMapper.writeValueAsBytes(model))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
