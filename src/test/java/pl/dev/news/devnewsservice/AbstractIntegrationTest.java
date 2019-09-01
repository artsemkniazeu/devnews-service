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
import pl.dev.news.devnewsservice.entity.CategoryEntity;
import pl.dev.news.devnewsservice.entity.TagEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.entity.UserRoleEntity;
import pl.dev.news.devnewsservice.exception.NotFoundException;
import pl.dev.news.devnewsservice.mapper.CategoryMapper;
import pl.dev.news.devnewsservice.mapper.TagMapper;
import pl.dev.news.devnewsservice.mapper.UserMapper;
import pl.dev.news.devnewsservice.repository.CategoryRepository;
import pl.dev.news.devnewsservice.repository.TagRepository;
import pl.dev.news.devnewsservice.repository.UserRepository;
import pl.dev.news.devnewsservice.security.impl.TokenProviderImpl;
import pl.dev.news.devnewsservice.security.impl.TokenValidatorImpl;
import pl.dev.news.devnewsservice.utils.TestUtils;
import pl.dev.news.model.rest.RestCategoryModel;
import pl.dev.news.model.rest.RestSignUpRequest;
import pl.dev.news.model.rest.RestTagModel;
import pl.dev.news.model.rest.RestUserModel;

import java.util.UUID;

import static pl.dev.news.devnewsservice.constants.ExceptionConstants.categoryNotFound;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.tagWithIdNotFound;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.userWithIdNotFound;

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

    @Autowired
    protected CategoryRepository categoryRepository;

    @Autowired
    protected TagRepository tagRepository;

    @After
    public final void clearDatabase() {
        jdbcTemplate.execute("delete from comments");
        jdbcTemplate.execute("delete from user_follower");
        jdbcTemplate.execute("delete from post_category");
        jdbcTemplate.execute("delete from post_tag");
        jdbcTemplate.execute("delete from user_bookmark");
        jdbcTemplate.execute("delete from user_tag");
        jdbcTemplate.execute("delete from group_user");
        jdbcTemplate.execute("delete from uploads");
        jdbcTemplate.execute("delete from categories");
        jdbcTemplate.execute("delete from tags");
        jdbcTemplate.execute("delete from posts");
        jdbcTemplate.execute("delete from groups");
        jdbcTemplate.execute("delete from users");
    }

    // User

    protected UserEntity createUser(
            final UserEntity user,
            final UserRoleEntity role
    ) {
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.saveAndFlush(user);
    }

    protected UserEntity createUser(
            final RestSignUpRequest signUpRequest,
            final UserRoleEntity role
    ) {
        return createUser(userMapper.toEntity(signUpRequest), role);
    }

    protected UserEntity createUser(
            final RestUserModel role,
            final UserRoleEntity entity
    ) {
        final UserEntity userEntity = userMapper.toEntity(TestUtils.restSignupRequest());
        final UserEntity updated = userMapper.update(userEntity, role);
        return createUser(updated, entity);
    }

    protected UserEntity createUser(final UserRoleEntity role) {
        return createUser(TestUtils.restUserModel(), role);
    }

    protected UserEntity getUser(final UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(userWithIdNotFound));
    }

    protected void deleteUser(final UUID userId) {
        userRepository.softDelete(userId);
    }


    // Category

    protected CategoryEntity createCategory(final RestCategoryModel model) {
        final CategoryMapper categoryMapper = CategoryMapper.INSTANCE;
        final CategoryEntity entity = categoryMapper.toEntity(model);
        return categoryRepository.saveAndFlush(entity);
    }

    protected CategoryEntity createCategory() {
        return createCategory(TestUtils.restCategoryModel());
    }

    protected CategoryEntity getCategory(final UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(categoryNotFound));
    }

    // Tag

    protected TagEntity createTag(final RestTagModel model) {
        final TagEntity entity = TagMapper.INSTANCE.toEntity(model);
        return tagRepository.saveAndFlush(entity);
    }

    protected TagEntity createTag() {
        return createTag(TestUtils.restTagModel());
    }

    protected TagEntity getTag(final UUID tagId) {
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new NotFoundException(tagWithIdNotFound));
    }

}
