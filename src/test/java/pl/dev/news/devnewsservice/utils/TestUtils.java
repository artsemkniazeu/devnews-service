package pl.dev.news.devnewsservice.utils;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;
import org.springframework.context.annotation.Profile;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;
import pl.dev.news.model.rest.RestLoginRequest;
import pl.dev.news.model.rest.RestRefreshTokenRequest;
import pl.dev.news.model.rest.RestSignUpRequest;
import pl.dev.news.model.rest.RestUserModel;
import pl.dev.news.model.rest.RestUserRole;

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

    public RestLoginRequest restLoginRequest(final RestSignUpRequest restSignupRequest) {
        return new RestLoginRequest()
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
}
