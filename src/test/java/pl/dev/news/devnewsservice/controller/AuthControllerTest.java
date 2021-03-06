package pl.dev.news.devnewsservice.controller;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import pl.dev.news.devnewsservice.AbstractIntegrationTest;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.utils.TestUtils;
import pl.dev.news.model.rest.RestRefreshTokenRequest;
import pl.dev.news.model.rest.RestSignInRequest;
import pl.dev.news.model.rest.RestSignUpRequest;
import pl.dev.news.model.rest.RestTokenResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;
import static pl.dev.news.controller.api.AuthApi.activatePath;
import static pl.dev.news.controller.api.AuthApi.refreshTokenPath;
import static pl.dev.news.controller.api.AuthApi.signInPath;
import static pl.dev.news.controller.api.AuthApi.signUpPath;
import static pl.dev.news.devnewsservice.entity.UserRoleEntity.USER;

public class AuthControllerTest extends AbstractIntegrationTest {

    @Test
    public void testSignUp() throws Exception {
        // given
        final RestSignUpRequest restSignUpRequest = TestUtils.restSignupRequest();
        // when
        mockMvc.perform(
                post(signUpPath)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restSignUpRequest)))
                // then
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    public void testSignUpConflict() throws Exception {
        // given
        final RestSignUpRequest restSignUpRequest = TestUtils.restSignupRequest();
        createUser(restSignUpRequest, USER);

        // when
        mockMvc.perform(
                post(signUpPath)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restSignUpRequest)))
                // then
                .andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    public void testSignIn() throws Exception {
        // given
        final RestSignUpRequest restSignUpRequest = TestUtils.restSignupRequest();
        final UserEntity userEntity = createUser(restSignUpRequest, USER);
        final RestSignInRequest restSignInRequest = TestUtils.restSignInRequest(restSignUpRequest);
        // when
        final MvcResult response = mockMvc.perform(post(signInPath)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restSignInRequest)))
                // then
                .andExpect(status().isOk())
                .andReturn();
        final RestTokenResponse model = objectMapper.readValue(
                response.getResponse().getContentAsString(), RestTokenResponse.class
        );
        final UserEntity result = tokenProvider.buildUserEntityByToken(model.getAccess().getToken());
        Assert.assertNotNull(model);
        Assert.assertEquals(userEntity.getId(), result.getId());
    }

    @Test
    public void testSignInBadCredentialsDisabled() throws Exception {
        // given
        final RestSignUpRequest restSignUpRequest = TestUtils.restSignupRequest();
        createUser(restSignUpRequest, USER, false, false);
        final RestSignInRequest restSignInRequest = TestUtils.restSignInRequest(restSignUpRequest);
        // when
        mockMvc.perform(post(signInPath)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restSignInRequest)))
                // then
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testSignInBadCredentialsLocked() throws Exception {
        // given
        final RestSignUpRequest restSignUpRequest = TestUtils.restSignupRequest();
        createUser(restSignUpRequest, USER, true, true);
        final RestSignInRequest restSignInRequest = TestUtils.restSignInRequest(restSignUpRequest);
        // when
        mockMvc.perform(post(signInPath)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restSignInRequest)))
                // then
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testSignInUserDeleted() throws Exception {
        // given
        final RestSignUpRequest restSignUpRequest = TestUtils.restSignupRequest();
        final UserEntity userEntity = createUser(restSignUpRequest, USER);
        final RestSignInRequest restSignInRequest = TestUtils.restSignInRequest(restSignUpRequest);
        deleteUser(userEntity.getId());
        // when
        mockMvc.perform(post(signInPath)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restSignInRequest)))
                // then
                .andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    public void testSignInUserWithEmailNotExists() throws Exception {
        // given
        final RestSignUpRequest restSignupRequest = TestUtils.restSignupRequest();
        final RestSignInRequest restSignInRequest = TestUtils.restSignInRequest(restSignupRequest);
        // when
        mockMvc.perform(post(signInPath)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restSignInRequest)))
                // then
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void testSignInIncorrectPassword() throws Exception {
        // given
        final RestSignUpRequest restSignUpRequest = TestUtils.restSignupRequest();
        createUser(restSignUpRequest, USER);
        final RestSignInRequest restSignInRequest = TestUtils.restSignInRequest(restSignUpRequest);
        restSignInRequest.password("incorrect");
        // when
        mockMvc.perform(post(signInPath)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restSignInRequest)))
                // then
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testRefreshToken() throws Exception {
        //given
        final UserEntity user = createUser(USER);
        final RestTokenResponse restTokenResponse = tokenProvider.createTokenModel(user);
        final RestRefreshTokenRequest request = TestUtils
                .restRefreshTokenRequest(restTokenResponse.getRefresh().getToken());
        //when
        final MvcResult response = mockMvc.perform(post(refreshTokenPath)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        final RestTokenResponse model = objectMapper.readValue(response.getResponse().getContentAsByteArray(),
                RestTokenResponse.class);
        //then
        Assert.assertNotNull(model);
        Assert.assertTrue(tokenValidator.validateAccessToken(model.getAccess().getToken()));
        Assert.assertTrue(tokenValidator.validateRefreshToken(model.getRefresh().getToken()));
    }

    @Test
    public void testRefreshTokenInvalid() throws Exception {
        //given
        final UserEntity user = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(user);
        final RestRefreshTokenRequest request = TestUtils
                .restRefreshTokenRequest(tokenResponse.getRefresh().getToken());
        request.setRefreshToken("invalid");
        //when
        mockMvc.perform(post(refreshTokenPath)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void testActivate() throws Exception {
        // given
        final RestSignUpRequest restSignUpRequest = TestUtils.restSignupRequest();
        final UserEntity userEntity = createUser(restSignUpRequest, USER, false, false);
        final String path = fromPath(activatePath)
                .queryParam("key", userEntity.getActivationKey())
                .toUriString();

        mockMvc.perform(
                get(path)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restSignUpRequest)))
                // then
                .andExpect(status().isNoContent())
                .andReturn();
    }

}
