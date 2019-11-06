package pl.dev.news.devnewsservice.controller;

import org.junit.Assert;
import org.junit.Test;
import pl.dev.news.devnewsservice.AbstractIntegrationTest;
import pl.dev.news.devnewsservice.entity.CategoryEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.utils.PathUtils;
import pl.dev.news.devnewsservice.utils.TestUtils;
import pl.dev.news.model.rest.RestCategoryModel;
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
import static pl.dev.news.controller.api.CategoryApi.createCategoryPath;
import static pl.dev.news.controller.api.CategoryApi.deleteCategoryPath;
import static pl.dev.news.controller.api.CategoryApi.getCategoriesPath;
import static pl.dev.news.controller.api.CategoryApi.getCategoryPath;
import static pl.dev.news.controller.api.CategoryApi.updateCategoryPath;
import static pl.dev.news.devnewsservice.entity.UserRoleEntity.USER;

public class CategoryControllerTest extends AbstractIntegrationTest {

    @Test
    public void testCreateCategory() throws Exception {
        // given
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenModel = tokenProvider.createTokenModel(user);
        final RestCategoryModel model = TestUtils.restCategoryModel();
        // when
        mockMvc.perform(
                post(createCategoryPath)
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, tokenModel.getAccess().getToken())
                        .content(objectMapper.writeValueAsBytes(model)))
                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void testDeleteCategory() throws Exception {
        // given
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenModel = tokenProvider.createTokenModel(user);
        final CategoryEntity entity = createCategory();
        // when
        mockMvc.perform(delete(deleteCategoryPath, entity.getId())
                .header(AUTHORIZATION, tokenModel.getAccess().getToken()))
                // then
                .andExpect(status().isNoContent());

        final CategoryEntity entityFromDB = getCategory(entity.getId());
        Assert.assertNotNull(entityFromDB.getDeletedAt());
    }

    @Test
    public void testDeleteCategoryNotFound() throws Exception {
        // given
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenModel = tokenProvider.createTokenModel(user);
        // when
        mockMvc.perform(delete(deleteCategoryPath, UUID.randomUUID())
                .header(AUTHORIZATION, tokenModel.getAccess().getToken()))
                // then
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetCategories() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        final CategoryEntity entity = createCategory();
        final CategoryEntity children = new ArrayList<>(entity.getChildren()).get(0);
        mockMvc.perform(
                get(PathUtils.generate(getCategoriesPath))
                        .param("name", children.getName())
                        .param("value", children.getValue())
                        .param("parentId", entity.getId().toString())
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",  hasSize(1)))
                .andExpect(jsonPath("$[?(@.id == '" + children.getId() + "')]").exists());
    }

    @Test
    public void testGetCategory() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        final CategoryEntity entity = createCategory();
        mockMvc.perform(
                get(PathUtils.generate(getCategoryPath, entity.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + entity.getId() + "')]").exists());
    }

    @Test
    public void testGetCategoryNotFound() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        mockMvc.perform(
                get(PathUtils.generate(getCategoryPath, UUID.randomUUID()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateCategory() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        final RestCategoryModel model = TestUtils.restCategoryModel();
        final CategoryEntity entity = createCategory(model);
        model.setName("changed");
        mockMvc.perform(
                put(PathUtils.generate(updateCategoryPath, entity.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .content(objectMapper.writeValueAsBytes(model))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + entity.getId() + "')]").exists())
                .andExpect(jsonPath("$[?(@.name == '" + model.getName() + "')]").exists());
    }

    @Test
    public void testUpdateCategoryNotFound() throws Exception {
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        final RestCategoryModel model = TestUtils.restCategoryModel();
        mockMvc.perform(
                put(PathUtils.generate(updateCategoryPath, UUID.randomUUID()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .content(objectMapper.writeValueAsBytes(model))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
