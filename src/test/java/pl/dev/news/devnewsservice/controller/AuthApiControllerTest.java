package pl.dev.news.devnewsservice.controller;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import pl.dev.news.devnewsservice.AbstractIntegrationTest;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.utils.TestUtils;
import pl.dev.news.model.rest.RestLoginRequest;
import pl.dev.news.model.rest.RestRefreshTokenRequest;
import pl.dev.news.model.rest.RestSignUpRequest;
import pl.dev.news.model.rest.RestTokenModel;
import pl.dev.news.model.rest.RestUserModel;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.dev.news.controller.api.AuthApi.refreshTokenPath;
import static pl.dev.news.controller.api.AuthApi.signInPath;
import static pl.dev.news.controller.api.AuthApi.signUpPath;

public class AuthApiControllerTest extends AbstractIntegrationTest {

    @Test
    public void testSignUp() throws Exception {
        // given
        final RestSignUpRequest signUpRequest = TestUtils.restSignupRequest();
        // when
        final MvcResult response = mockMvc.perform(
                post(signUpPath)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                // then
                .andExpect(status().isCreated())
                .andReturn();

        final RestUserModel model = objectMapper
                .readValue(response.getResponse().getContentAsString(), RestUserModel.class);
        Assert.assertNotNull(model);
        Assert.assertEquals(signUpRequest.getEmail(), model.getEmail());
    }

    @Test
    public void testLogin() throws Exception {
        // given
        final RestSignUpRequest restSignupRequest = TestUtils.restSignupRequest();
        final UserEntity userEntity = createUser(restSignupRequest);
        final RestLoginRequest restLoginRequest = TestUtils.restLoginRequest(restSignupRequest);
        // when
        final MvcResult response = mockMvc.perform(post(signInPath)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restLoginRequest)))
                // then
                .andExpect(status().isCreated())
                .andReturn();
        final RestTokenModel model = objectMapper.readValue(
                response.getResponse().getContentAsString(), RestTokenModel.class
        );
        final UserEntity result = tokenProvider.buildUserEntityByToken(model.getAccessToken());
        Assert.assertNotNull(model);
        Assert.assertEquals(userEntity.getId(), result.getId());
    }

    @Test
    public void testLoginUserDeleted() throws Exception {
        // given
        final RestSignUpRequest restSignupRequest = TestUtils.restSignupRequest();
        final UserEntity userEntity = createUser(restSignupRequest);
        final RestLoginRequest restLoginRequest = TestUtils.restLoginRequest(restSignupRequest);
        deleteUser(userEntity.getId());
        // when
        mockMvc.perform(post(signInPath)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restLoginRequest)))
                // then
                .andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    public void testLoginUserWithEmailNotExists() throws Exception {
        // given
        final RestSignUpRequest restSignupRequest = TestUtils.restSignupRequest();
        final RestLoginRequest restLoginRequest = TestUtils.restLoginRequest(restSignupRequest);
        // when
        mockMvc.perform(post(signInPath)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restLoginRequest)))
                // then
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void testLoginIncorrectPassword() throws Exception {
        // given
        final RestSignUpRequest restSignupRequest = TestUtils.restSignupRequest();
        createUser(restSignupRequest);
        final RestLoginRequest restLoginRequest = TestUtils.restLoginRequest(restSignupRequest);
        restLoginRequest.password("incorrect");
        // when
        mockMvc.perform(post(signInPath)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restLoginRequest)))
                // then
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testRefreshToken() throws Exception {
        //given
        final UserEntity user = createUser();
        final RestTokenModel tokenModel = tokenProvider.createTokenModel(user);
        final RestRefreshTokenRequest request = TestUtils.restRefreshTokenRequest(tokenModel.getRefreshToken());
        //when
        final MvcResult response = mockMvc.perform(post(refreshTokenPath)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        final RestTokenModel responseTokenModel = objectMapper.readValue(response.getResponse().getContentAsByteArray(),
                RestTokenModel.class);
        //then
        Assert.assertNotNull(responseTokenModel);
        Assert.assertTrue(tokenValidator.validateAccessToken(responseTokenModel.getAccessToken()));
        Assert.assertTrue(tokenValidator.validateRefreshToken(responseTokenModel.getRefreshToken()));
    }

    @Test
    public void testRefreshTokenInvalid() throws Exception {
        //given
        final UserEntity user = createUser();
        final RestTokenModel tokenModel = tokenProvider.createTokenModel(user);
        final RestRefreshTokenRequest request = TestUtils.restRefreshTokenRequest(tokenModel.getRefreshToken());
        request.setRefreshToken("invalid");
        //when
        mockMvc.perform(post(refreshTokenPath)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

}
