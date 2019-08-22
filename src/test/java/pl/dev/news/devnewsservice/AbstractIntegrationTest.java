package pl.dev.news.devnewsservice;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.entity.UserRoleEntity;
import pl.dev.news.devnewsservice.exception.NotFoundException;
import pl.dev.news.devnewsservice.mapper.UserMapper;
import pl.dev.news.devnewsservice.repository.UserRepository;
import pl.dev.news.devnewsservice.security.impl.TokenProviderImpl;
import pl.dev.news.devnewsservice.security.impl.TokenValidatorImpl;
import pl.dev.news.devnewsservice.utils.TestUtils;
import pl.dev.news.model.rest.RestSignUpRequest;
import pl.dev.news.model.rest.RestUserModel;

import java.util.UUID;

import static pl.dev.news.devnewsservice.exception.ExceptionsConstants.userNotFound;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {
        PostgresInitializer.class
})
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    protected final UserMapper userMapper = UserMapper.INSTANCE;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected TokenProviderImpl tokenProvider;

    @Autowired
    protected TokenValidatorImpl tokenValidator;

    @Autowired
    protected UserRepository userRepository;

    @Before
    public final void clearDatabase() {
        jdbcTemplate.execute("delete from categories");
        jdbcTemplate.execute("delete from tags");
        jdbcTemplate.execute("delete from users");
        jdbcTemplate.execute("delete from comments");
        jdbcTemplate.execute("delete from posts");
        jdbcTemplate.execute("delete from post_category");
        jdbcTemplate.execute("delete from post_tag");
        jdbcTemplate.execute("delete from user_bookmark");
        jdbcTemplate.execute("delete from user_follower");
        jdbcTemplate.execute("delete from user_tag");
        jdbcTemplate.execute("delete from uploads");
    }

    // User logic

    protected UserEntity createUser(
            final UserEntity userEntity,
            final UserRoleEntity userRoleEntity
    ) {
        userEntity.setRole(userRoleEntity);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        return userRepository.saveAndFlush(userEntity);
    }

    protected UserEntity createUser(
            final RestSignUpRequest restSignUpRequest,
            final UserRoleEntity userRoleEntity
    ) {
        return createUser(userMapper.toEntity(restSignUpRequest), userRoleEntity);
    }

    protected UserEntity createUser(
            final RestUserModel restUserModel,
            final UserRoleEntity userRoleEntity
    ) {
        final UserEntity userEntity = userMapper.toEntity(TestUtils.restSignupRequest());
        final UserEntity updated = userMapper.update(userEntity, restUserModel);
        return createUser(updated, userRoleEntity);
    }

    protected UserEntity createUser(final UserRoleEntity userRoleEntity) {
        return createUser(TestUtils.restUserModel(), userRoleEntity);
    }

    @Transactional(readOnly = true)
    protected UserEntity getUser(final UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(userNotFound));
    }

    @Transactional
    protected void deleteUser(final UUID userId) {
        userRepository.softDelete(userId);
    }

}
