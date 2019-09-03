package pl.dev.news.devnewsservice.utils;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;
import org.springframework.context.annotation.Profile;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;
import pl.dev.news.model.rest.RestCategoryModel;
import pl.dev.news.model.rest.RestRefreshTokenRequest;
import pl.dev.news.model.rest.RestSignInRequest;
import pl.dev.news.model.rest.RestSignUpRequest;
import pl.dev.news.model.rest.RestTagModel;
import pl.dev.news.model.rest.RestUserModel;
import pl.dev.news.model.rest.RestUserRole;

import java.util.List;
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

    public RestTagModel restTagModel() {
        final String name = faker.name().title();
        return new RestTagModel()
                .name(name);
    }


    public int amount(final Integer... a) {
        int x = 5;
        if (a.length > 0) {
            x = a[0];
        }
        return x;
    }
}
