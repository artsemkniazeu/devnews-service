package pl.dev.news.devnewsservice.controller;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import pl.dev.news.devnewsservice.AbstractIntegrationTest;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.utils.PathUtils;
import pl.dev.news.devnewsservice.utils.TestUtils;
import pl.dev.news.model.rest.RestEmailModel;
import pl.dev.news.model.rest.RestTokenResponse;
import pl.dev.news.model.rest.RestUserModel;
import pl.dev.news.model.rest.RestUserQueryParameters;

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
import static pl.dev.news.controller.api.UserApi.changeEmailAddressPath;
import static pl.dev.news.controller.api.UserApi.deleteUserPath;
import static pl.dev.news.controller.api.UserApi.followUserPath;
import static pl.dev.news.controller.api.UserApi.getUserPath;
import static pl.dev.news.controller.api.UserApi.getUsersPath;
import static pl.dev.news.controller.api.UserApi.resendActivationCodePath;
import static pl.dev.news.controller.api.UserApi.unfollowUserPath;
import static pl.dev.news.controller.api.UserApi.updateUserPath;
import static pl.dev.news.controller.api.UserApi.uploadBackgroundPath;
import static pl.dev.news.controller.api.UserApi.uploadImagePath;
import static pl.dev.news.devnewsservice.entity.UserRoleEntity.ADMIN;
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
    public void testDeleteUserForbidden() throws Exception {
        final UserEntity userEntity = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(userEntity);
        mockMvc.perform(
                delete(PathUtils.generate(deleteUserPath, "fed443a9-bb8d-4320-848d-9d1c18bd026f"))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteUserNotFound() throws Exception {
        final UserEntity userEntity = createUser(ADMIN);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(userEntity);
        mockMvc.perform(
                delete(PathUtils.generate(deleteUserPath, "fed443a9-bb8d-4320-848d-9d1c18bd026f"))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
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
    public void testGetUserNotFound() throws Exception {
        final UserEntity expected = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(expected);
        mockMvc.perform(
                get(PathUtils.generate(getUserPath, "fed443a9-bb8d-4320-848d-9d1c18bd026f"))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
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

    @Test
    public void testUpdateUser() throws Exception {
        final RestUserModel restUserModel = TestUtils.restUserModel();
        final UserEntity expected = createUser(restUserModel, USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(expected);
        restUserModel.setEmail("changed@example.com");
        restUserModel.setFirstName("changed");
        mockMvc.perform(
                put(PathUtils.generate(updateUserPath, expected.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restUserModel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + expected.getId() + "')]").exists())
                .andExpect(jsonPath("$[?(@.email == '" + expected.getEmail() + "')]").exists())
                .andExpect(jsonPath("$[?(@.firstName == '" + restUserModel.getFirstName() + "')]").exists());
    }

    @Test
    public void testUpdateUserNotFound() throws Exception {
        final RestUserModel restUserModel = TestUtils.restUserModel();
        final UserEntity expected = createUser(restUserModel, USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(expected);
        mockMvc.perform(
                put(PathUtils.generate(updateUserPath, UUID.randomUUID()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restUserModel)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUploadImage() throws Exception {
        // given
        final RestUserModel restUserModel = TestUtils.restUserModel();
        final UserEntity expected = createUser(restUserModel, USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(expected);
        final MockMultipartFile file = TestUtils.getMultipartFile("avatar.jpg", "image/jpeg");

        final String url = "https://example.com/image.jpg";
        mockBucketUpload(url);
        // when
        mockMvc.perform(
                multipart(PathUtils.generate(uploadImagePath, expected.getId()))
                        .file(file)
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken()))
                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[?(@.id)]").exists())
                .andExpect(jsonPath("$[?(@.url == '" + url + "')]").exists());

        final UserEntity userEntityFromDb = getUser(expected.getId());
        Assert.assertEquals(userEntityFromDb.getImageUrl(), url);
    }

    @Test
    public void testUploadImageNotFound() throws Exception {
        // given
        final RestUserModel restUserModel = TestUtils.restUserModel();
        final UserEntity expected = createUser(restUserModel, USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(expected);
        final MockMultipartFile file = TestUtils.getMultipartFile("avatar.jpg", "image/jpeg");

        final String url = "https://example.com/image.jpg";
        mockBucketUpload(url);
        // when
        mockMvc.perform(
                multipart(PathUtils.generate(uploadImagePath, UUID.randomUUID()))
                        .file(file)
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken()))
                // then
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUploadBackground() throws Exception {
        // given
        final RestUserModel restUserModel = TestUtils.restUserModel();
        final UserEntity expected = createUser(restUserModel, USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(expected);
        final MockMultipartFile file = TestUtils.getMultipartFile("avatar.jpg", "image/jpeg");

        final String url = "https://example.com/image.jpg";
        mockBucketUpload(url);
        // when
        mockMvc.perform(
                multipart(PathUtils.generate(uploadBackgroundPath, expected.getId()))
                        .file(file)
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken()))
                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[?(@.id)]").exists())
                .andExpect(jsonPath("$[?(@.url == '" + url + "')]").exists());

        final UserEntity userEntityFromDb = getUser(expected.getId());
        Assert.assertEquals(userEntityFromDb.getBgUrl(), url);
    }

    @Test
    public void testUploadBackgroundNotFound() throws Exception {
        // given
        final RestUserModel restUserModel = TestUtils.restUserModel();
        final UserEntity expected = createUser(restUserModel, USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(expected);
        final MockMultipartFile file = TestUtils.getMultipartFile("avatar.jpg", "image/jpeg");

        final String url = "https://example.com/image.jpg";
        mockBucketUpload(url);
        // when
        mockMvc.perform(
                multipart(PathUtils.generate(uploadBackgroundPath, UUID.randomUUID()))
                        .file(file)
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken()))
                // then
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUploadImageBadRequest() throws Exception {
        // given
        final RestUserModel restUserModel = TestUtils.restUserModel();
        final UserEntity expected = createUser(restUserModel, USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(expected);
        final MockMultipartFile file = TestUtils.getMultipartFile("avatar.jpg", "video/mp4");

        final String url = "https://example.com/image.jpg";
        mockBucketUpload(url);
        // when
        mockMvc.perform(
                multipart(PathUtils.generate(uploadImagePath, expected.getId()))
                        .file(file)
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken()))
                // then
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFollowUnFollowUser() throws Exception {
        final UserEntity userEntity = createUser(USER);
        final UserEntity follower = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(userEntity);
        final RestUserQueryParameters parameters = new RestUserQueryParameters();

        mockMvc.perform(
                post(PathUtils.generate(followUserPath, follower.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        Assert.assertTrue(
                userResourcesService
                        .getFollowers(follower.getId(), parameters, 1, 10)
                        .getContent()
                .contains(userMapper.toModel(userEntity))
        );

        mockMvc.perform(
                delete(PathUtils.generate(unfollowUserPath, follower.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        Assert.assertTrue(
                userResourcesService
                        .getFollowers(follower.getId(), parameters, 1, 10)
                        .getContent()
                .isEmpty()
        );
    }

    @Test
    public void testFollowUnFollowUserNotFound() throws Exception {
        final UserEntity userEntity = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(userEntity);
        mockMvc.perform(
                post(PathUtils.generate(followUserPath, UUID.randomUUID()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());

        mockMvc.perform(
                delete(PathUtils.generate(unfollowUserPath, UUID.randomUUID()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testResendActivationCode() throws Exception {
        final UserEntity user = createUser(USER, false);
        mockMvc.perform(
                get(PathUtils.generate(resendActivationCodePath, user.getEmail())))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testResendActivationCodeNotFound() throws Exception {
        mockMvc.perform(
                get(PathUtils.generate(resendActivationCodePath, "notfound@example.com")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testResendActivationCodeBadRequest() throws Exception {
        final UserEntity user = createUser(USER, true);
        mockMvc.perform(
                get(PathUtils.generate(resendActivationCodePath, user.getEmail())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testChangeEmailAddress() throws Exception {
        final UserEntity userEntity = createUser(USER);
        final RestEmailModel model = TestUtils.restEmailModel();
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(userEntity);
        mockMvc.perform(
                post(PathUtils.generate(changeEmailAddressPath, userEntity.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(model)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testChangeEmailAddressConflict() throws Exception {
        final UserEntity userEntity = createUser(USER);
        final UserEntity exists = createUser(USER);
        final RestEmailModel model = TestUtils.restEmailModel();
        model.setEmail(exists.getEmail());
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(userEntity);
        mockMvc.perform(
                post(PathUtils.generate(changeEmailAddressPath, userEntity.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(model)))
                .andExpect(status().isConflict());
    }


}
