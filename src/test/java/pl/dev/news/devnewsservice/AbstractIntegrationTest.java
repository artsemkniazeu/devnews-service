package pl.dev.news.devnewsservice;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import org.junit.After;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.dev.news.devnewsservice.entity.CategoryEntity;
import pl.dev.news.devnewsservice.entity.CommentEntity;
import pl.dev.news.devnewsservice.entity.GroupEntity;
import pl.dev.news.devnewsservice.entity.PostEntity;
import pl.dev.news.devnewsservice.entity.TagEntity;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.entity.UserRoleEntity;
import pl.dev.news.devnewsservice.exception.NotFoundException;
import pl.dev.news.devnewsservice.mapper.CategoryMapper;
import pl.dev.news.devnewsservice.mapper.CommentMapper;
import pl.dev.news.devnewsservice.mapper.GroupMapper;
import pl.dev.news.devnewsservice.mapper.PostMapper;
import pl.dev.news.devnewsservice.mapper.TagMapper;
import pl.dev.news.devnewsservice.mapper.UserMapper;
import pl.dev.news.devnewsservice.repository.CategoryRepository;
import pl.dev.news.devnewsservice.repository.CommentRepository;
import pl.dev.news.devnewsservice.repository.GroupRepository;
import pl.dev.news.devnewsservice.repository.PostRepository;
import pl.dev.news.devnewsservice.repository.TagRepository;
import pl.dev.news.devnewsservice.repository.UserRepository;
import pl.dev.news.devnewsservice.security.impl.TokenProviderImpl;
import pl.dev.news.devnewsservice.security.impl.TokenValidatorImpl;
import pl.dev.news.devnewsservice.service.GoogleFileService;
import pl.dev.news.devnewsservice.service.TwilioService;
import pl.dev.news.devnewsservice.service.UserResourcesService;
import pl.dev.news.devnewsservice.service.UserService;
import pl.dev.news.devnewsservice.utils.TestUtils;
import pl.dev.news.model.rest.RestCategoryModel;
import pl.dev.news.model.rest.RestCommentModel;
import pl.dev.news.model.rest.RestGroupModel;
import pl.dev.news.model.rest.RestPostModel;
import pl.dev.news.model.rest.RestSignUpRequest;
import pl.dev.news.model.rest.RestTagModel;
import pl.dev.news.model.rest.RestUserModel;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.categoryWithIdNotFound;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.commentWithIdNotFound;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.groupWithIdNotFound;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.postWithIdNotFound;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.tagWithIdNotFound;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.userWithIdNotFound;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {
        PostgresInitializer.class,
        MaildevInitializer.class
})
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    protected final UserMapper userMapper = UserMapper.INSTANCE;

    protected final PostMapper postMapper = PostMapper.INSTANCE;

    protected final CommentMapper commentMapper = CommentMapper.INSTANCE;

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

    @Autowired
    protected PostRepository postRepository;

    @Autowired
    protected GroupRepository groupRepository;

    @Autowired
    protected CommentRepository commentRepository;

    @Autowired
    protected UserService userService;

    @Autowired
    protected UserResourcesService userResourcesService;

    @MockBean
    protected Storage storage;

    @MockBean
    protected GoogleFileService fileService;

    @MockBean
    protected TwilioService twilioService;

    @After
    public final void clearDatabase() {
        jdbcTemplate.execute("truncate table comments restart identity cascade");
        jdbcTemplate.execute("truncate table uploads restart identity cascade");
        jdbcTemplate.execute("truncate table post_category restart identity cascade");
        jdbcTemplate.execute("truncate table categories restart identity cascade");
        jdbcTemplate.execute("truncate table post_tag restart identity cascade");
        jdbcTemplate.execute("truncate table user_bookmark restart identity cascade");
        jdbcTemplate.execute("truncate table posts restart identity cascade");
        jdbcTemplate.execute("truncate table user_follower restart identity cascade");
        jdbcTemplate.execute("truncate table user_tag restart identity cascade");
        jdbcTemplate.execute("truncate table tags restart identity cascade");
        jdbcTemplate.execute("truncate table group_user restart identity cascade");
        jdbcTemplate.execute("truncate table groups restart identity cascade");
        jdbcTemplate.execute("truncate table users restart identity cascade");
    }

    protected void mockBucketUpload(final String url) {
        final Blob blob = Mockito.mock(Blob.class);
        final BlobId blobId = BlobId.of("", "", 1L);
        when(blob.getMediaLink()).thenReturn(url);
        when(blob.getBlobId()).thenReturn(blobId);
        when(fileService.bucketUpload(any(), any()))
                .thenReturn(blob);
    }

    // User

    protected UserEntity createUser(
            final UserEntity user,
            final UserRoleEntity role,
            final boolean enabled,
            final boolean locked
    ) {
        user.setEnabled(enabled);
        user.setLocked(locked);
        if (!enabled) {
            user.setActivationKey(UUID.randomUUID());
        }
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.saveAndFlush(user);
    }

    protected UserEntity createUser(
            final UserEntity user,
            final UserRoleEntity role,
            final boolean enabled
    ) {
        return createUser(user, role, enabled, false);
    }

    protected UserEntity createUser(
            final RestSignUpRequest signUpRequest,
            final UserRoleEntity role
    ) {
        return createUser(userMapper.toEntity(signUpRequest), role, true);
    }

    protected UserEntity createUser(
            final RestSignUpRequest signUpRequest,
            final UserRoleEntity role,
            final boolean enabled,
            final boolean locked
    ) {
        return createUser(userMapper.toEntity(signUpRequest), role, enabled, locked);
    }

    protected UserEntity createUser(
            final RestUserModel role,
            final UserRoleEntity entity,
            final boolean enabled
    ) {
        final UserEntity userEntity = userMapper.toEntity(TestUtils.restSignupRequest());
        final UserEntity updated = userMapper.update(userEntity, role);
        return createUser(updated, entity, enabled);
    }

    protected UserEntity createUser(
            final RestUserModel role,
            final UserRoleEntity entity
    ) {
        final UserEntity userEntity = userMapper.toEntity(TestUtils.restSignupRequest());
        final UserEntity updated = userMapper.update(userEntity, role);
        return createUser(updated, entity, true);
    }

    protected UserEntity createUser(final UserRoleEntity role) {
        return createUser(TestUtils.restUserModel(), role, true);
    }

    protected UserEntity createUser(final UserRoleEntity role, final boolean enabled) {
        return createUser(TestUtils.restUserModel(), role, enabled);
    }

    protected UserEntity getUser(final UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(userWithIdNotFound, userId));
    }

    protected void deleteUser(final UUID userId) {
        userRepository.softDeleteById(userId);
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
                .orElseThrow(() -> new NotFoundException(categoryWithIdNotFound, categoryId));
    }

    // Tag

    protected TagEntity createTag(final RestTagModel model) {
        final TagEntity entity = TagMapper.INSTANCE.toEntity(model);
        return tagRepository.saveAndFlush(entity);
    }

    protected TagEntity createTag() {
        return createTag(TestUtils.restTagModel());
    }

    protected List<TagEntity> createTags(final Integer... amount) {
        return IntStream.range(0, TestUtils.amount(amount))
                .mapToObj(i -> createTag())
                .collect(Collectors.toList());
    }

    protected TagEntity getTag(final UUID tagId) {
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new NotFoundException(tagWithIdNotFound, tagId));
    }


    // Post

    protected PostEntity createPost(final UserEntity user, final RestPostModel model, final GroupEntity group) {
        final PostEntity entity = PostMapper.INSTANCE.toEntity(model, user, group);
        final PostEntity persist = postRepository.save(entity);
        PostMapper.INSTANCE.update(persist, model);
        return postRepository.saveAndFlush(persist);
    }

    protected PostEntity createPost(final UserEntity user) {
        final GroupEntity group = createGroup(user);
        final RestPostModel model = TestUtils.restPostModel(createTags(), createCategory().getChildren(), group);
        return createPost(user, model, null);
    }

    protected PostEntity createPostEmpty(final UserEntity user) {
        final GroupEntity group = createGroup(user);
        final RestPostModel model = TestUtils
                .restPostModel(createTags(0), new HashSet<>(), group);
        return createPost(user, model, group);
    }

    protected PostEntity createPostEmpty(final UserEntity user, final GroupEntity group) {
        final RestPostModel model = TestUtils
                .restPostModel(createTags(0), new HashSet<>(), group);
        return createPost(user, model, group);
    }

    protected PostEntity getPost(final UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(postWithIdNotFound, postId));
    }


    // Group

    protected GroupEntity createGroup(final UserEntity user, final RestGroupModel model) {
        final GroupEntity entity = GroupMapper.INSTANCE.toEntity(model);
        entity.setOwner(user);
        return groupRepository.saveAndFlush(entity);
    }

    protected GroupEntity createGroup(final UserEntity user) {
        return createGroup(user, TestUtils.restGroupModel(user.getId()));
    }

    protected GroupEntity getGroup(final UUID groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException(groupWithIdNotFound, groupId));
    }

    protected GroupEntity followGroup(final UserEntity user, final GroupEntity groupEntity) {
        groupEntity.addFollower(user);
        return groupRepository.saveAndFlush(groupEntity);
    }


    // Comment

    protected CommentEntity createComment(
            final UserEntity user,
            final PostEntity post,
            final RestCommentModel model
    ) {
        final CommentEntity entity = commentMapper.toEntity(model);
        entity.setUser(user);
        entity.setPost(post);
        return commentRepository.saveAndFlush(entity);
    }

    protected CommentEntity createComment(
            final UserEntity user,
            final PostEntity post
    ) {
        final RestCommentModel model = TestUtils.restCommentModel(post.getId());
        return createComment(user, post, model);
    }

    protected CommentEntity createComment(
            final UserEntity user
    ) {
        final PostEntity post = createPost(user);
        return createComment(user, post);
    }

    protected CommentEntity getComment(final UUID commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(commentWithIdNotFound, commentId));
    }
}
