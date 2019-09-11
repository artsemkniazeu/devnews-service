package pl.dev.news.devnewsservice.utils;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;
import org.springframework.context.annotation.Profile;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;
import pl.dev.news.devnewsservice.entity.CategoryEntity;
import pl.dev.news.devnewsservice.entity.GroupEntity;
import pl.dev.news.devnewsservice.entity.TagEntity;
import pl.dev.news.devnewsservice.mapper.CategoryMapper;
import pl.dev.news.devnewsservice.mapper.TagMapper;
import pl.dev.news.model.rest.RestCategoryModel;
import pl.dev.news.model.rest.RestGroupModel;
import pl.dev.news.model.rest.RestPostModel;
import pl.dev.news.model.rest.RestRefreshTokenRequest;
import pl.dev.news.model.rest.RestSignInRequest;
import pl.dev.news.model.rest.RestSignUpRequest;
import pl.dev.news.model.rest.RestTagModel;
import pl.dev.news.model.rest.RestUserModel;
import pl.dev.news.model.rest.RestUserRole;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Profile("test")
@UtilityClass
public class TestUtils {

    private final Faker faker = Faker.instance();

    public RestSignUpRequest restSignupRequest() {
        return new RestSignUpRequest()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .password(RandomStringUtils.random(10, true, true));
    }

    public RestSignInRequest restSignInRequest(final RestSignUpRequest restSignupRequest) {
        return new RestSignInRequest()
                .email(restSignupRequest.getEmail())
                .password(restSignupRequest.getPassword());
    }

    public RestRefreshTokenRequest restRefreshTokenRequest(final String refreshToken) {
        return new RestRefreshTokenRequest()
                .refreshToken(refreshToken);
    }

    public RestUserModel restUserModel() {
        return new RestUserModel()
                .username(faker.name().username())
                .email(faker.internet().emailAddress())
                .role(RestUserRole.USER)
                .phone(faker.phoneNumber().cellPhone())
                .imageUrl(faker.internet().url())
                .bgUrl(faker.internet().url())
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .birthday(faker.date().birthday().toInstant().toString())
                .country(faker.address().country())
                .city(faker.address().city());
    }


    public RestCategoryModel restCategoryModel(final Integer... amount) {
        final List<RestCategoryModel> children = IntStream
                .range(0, amount(amount))
                .mapToObj(i -> restCategoryModel((List<RestCategoryModel>) null))
                .collect(Collectors.toList());
        return restCategoryModel(children);
    }

    public RestCategoryModel restCategoryModel(final List<RestCategoryModel> children) {
        return restCategoryModel(null, children);
    }

    public RestCategoryModel restCategoryModel(final UUID parentId, final List<RestCategoryModel> children) {
        final String name = faker.name().title();
        final String value = name.replace(' ', '_');
        return new RestCategoryModel()
                .name(name)
                .value(value)
                .parentId(parentId)
                .children(children);
    }

    public List<RestTagModel> restTagModels(final Integer... amount) {
        return IntStream
                .range(0, amount(amount))
                .mapToObj(i -> restTagModel())
                .collect(Collectors.toList());
    }

    public RestTagModel restTagModel() {
        final String name = faker.name().title();
        return new RestTagModel()
                .name(name);
    }

    public static RestPostModel restPostModel(
            final List<RestTagModel> tags,
            final List<RestCategoryModel> categories,
            final UUID groupId
    ) {
        return new RestPostModel()
                .title(faker.name().title())
                .text(faker.lorem().paragraph())
                .imageUrl(faker.internet().url())
                .publishDate(Instant.now().toString())
                .tags(tags)
                .categories(categories)
                .groupId(groupId);
    }

    public static RestPostModel restPostModel(
            final List<TagEntity> tagEntities,
            final Set<CategoryEntity> categoryEntities,
            final GroupEntity groupEntity
    ) {
        final List<RestTagModel> tags = tagEntities.stream()
                .map(TagMapper.INSTANCE::toModel)
                .collect(Collectors.toList());

        final List<RestCategoryModel> categories = categoryEntities.stream()
                .map(CategoryMapper.INSTANCE::toModel)
                .collect(Collectors.toList());

        return restPostModel(tags, categories, groupEntity.getId());

    }

    public static RestGroupModel restGroupModel(final UUID ownerId) {
        final String name = faker.name().title();
        final String value = name.replace(' ', '_');
        return new RestGroupModel()
                .name(name)
                .value(value)
                .about(faker.lorem().paragraph())
                .nsfw(false)
                .ownerId(ownerId);
    }

    public int amount(final Integer... a) {
        int x = 5;
        if (a.length > 0) {
            x = a[0];
        }
        return x;
    }
}
