package pl.dev.news.devnewsservice.controller;

import org.junit.Test;
import pl.dev.news.devnewsservice.AbstractIntegrationTest;
import pl.dev.news.devnewsservice.entity.GroupEntity;
import pl.dev.news.devnewsservice.entity.PostEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.utils.PathUtils;
import pl.dev.news.model.rest.RestPostQueryParameters;
import pl.dev.news.model.rest.RestTokenResponse;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.dev.news.controller.api.PostApi.bookmarkPostPath;
import static pl.dev.news.controller.api.UserApi.followUserPath;
import static pl.dev.news.controller.api.UserResourcesApi.getUserBookmarksPath;
import static pl.dev.news.controller.api.UserResourcesApi.getUserFollowersPath;
import static pl.dev.news.controller.api.UserResourcesApi.getUserFollowingPath;
import static pl.dev.news.controller.api.UserResourcesApi.getUsersFeedPath;
import static pl.dev.news.devnewsservice.entity.UserRoleEntity.USER;

public class UserResourcesControllerTest extends AbstractIntegrationTest {

    @Test
    public void testGetUserBookmarks() throws Exception {
        final UserEntity userEntity = createUser(USER);
        final PostEntity postEntity = createPostEmpty(userEntity);
        final RestPostQueryParameters parameters = new RestPostQueryParameters();
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(userEntity);


        mockMvc.perform(
                post(PathUtils.generate(bookmarkPostPath, postEntity.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(
                get(PathUtils.generate(getUserBookmarksPath, userEntity.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(parameters)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",  hasSize(1)))
                .andExpect(jsonPath("$[?(@.id == '" + postEntity.getId() + "')]").exists());
    }

    @Test
    public void testGetUserFeed() throws Exception {
        final UserEntity userEntity = createUser(USER);
        final UserEntity creator = createUser(USER);
        final GroupEntity group = createGroup(creator);
        final PostEntity postEntity = createPostEmpty(creator, group);
        final RestPostQueryParameters parameters = new RestPostQueryParameters();
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(userEntity);
        followGroup(userEntity, group);


        mockMvc.perform(
                get(PathUtils.generate(getUsersFeedPath, userEntity.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(parameters)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",  hasSize(1)))
                .andExpect(jsonPath("$[?(@.id == '" + postEntity.getId() + "')]").exists());
    }

    @Test
    public void testGetUserFollowers() throws Exception {
        final UserEntity userEntity = createUser(USER);
        final UserEntity follower = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(userEntity);

        mockMvc.perform(
                post(PathUtils.generate(followUserPath, follower.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());


        mockMvc.perform(
                get(PathUtils.generate(getUserFollowersPath, follower.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",  hasSize(1)))
                .andExpect(jsonPath("$[?(@.id == '" + userEntity.getId() + "')]").exists());

    }

    @Test
    public void testGetUserFollowing() throws Exception {
        final UserEntity userEntity = createUser(USER);
        final UserEntity follower = createUser(USER);
        final RestTokenResponse tokenResponse = tokenProvider.createTokenModel(userEntity);

        mockMvc.perform(
                post(PathUtils.generate(followUserPath, follower.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());


        mockMvc.perform(
                get(PathUtils.generate(getUserFollowingPath, userEntity.getId()))
                        .header(AUTHORIZATION, tokenResponse.getAccess().getToken())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$",  hasSize(1)))
                .andExpect(jsonPath("$[?(@.id == '" + follower.getId() + "')]").exists());
    }

}
