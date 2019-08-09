package pl.dev.news.devnewsservice;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
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

    @After
    public final void clearDatabase() {
        jdbcTemplate.execute("DELETE FROM users");
    }

    // User logic

    @Transactional
    protected UserEntity createUser(
            final RestSignUpRequest restSignupRequest,
            final UserRoleEntity userRoleEntity
    ) {
        final UserEntity userEntity = UserMapper.INSTANCE.toEntityFromRestSignupRequest(restSignupRequest);
        userEntity.setRole(userRoleEntity);
        userEntity.setPassword(passwordEncoder.encode(restSignupRequest.getPassword()));
        return userRepository.saveAndFlush(userEntity);
    }

    protected UserEntity createUser(final RestSignUpRequest restSignupRequest) {
        return createUser(restSignupRequest, UserRoleEntity.USER);
    }

    protected UserEntity createUser(final UserRoleEntity userRoleEntity) {
        return createUser(TestUtils.restSignupRequest(), userRoleEntity);
    }

    protected UserEntity createUser() {
        return createUser(TestUtils.restSignupRequest());
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
