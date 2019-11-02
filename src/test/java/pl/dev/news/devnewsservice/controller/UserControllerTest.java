package pl.dev.news.devnewsservice.controller;


import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import pl.dev.news.devnewsservice.AbstractIntegrationTest;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.utils.PathUtils;
import pl.dev.news.devnewsservice.utils.TestUtils;
import pl.dev.news.model.rest.RestTokenResponse;
import pl.dev.news.model.rest.RestUserModel;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.dev.news.controller.api.UserApi.deleteUserPath;
import static pl.dev.news.controller.api.UserApi.getUserPath;
import static pl.dev.news.controller.api.UserApi.getUsersPath;
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

    private void mockBucketUpload(final String url) {
        final Blob blob = Mockito.mock(Blob.class);
        final BlobId blobId = BlobId.of("", "", 1L);
        when(blob.getMediaLink()).thenReturn(url);
        when(blob.getBlobId()).thenReturn(blobId);
        when(fileService.bucketUpload(any(), any()))
                .thenReturn(blob);
    }
}
