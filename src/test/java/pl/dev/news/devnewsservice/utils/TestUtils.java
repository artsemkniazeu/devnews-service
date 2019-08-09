package pl.dev.news.devnewsservice.utils;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;
import org.springframework.context.annotation.Profile;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;
import pl.dev.news.model.rest.RestLoginRequest;
import pl.dev.news.model.rest.RestRefreshTokenRequest;
import pl.dev.news.model.rest.RestSignUpRequest;
import pl.dev.news.model.rest.RestUserModel;

@Profile("test")
@UtilityClass
public class TestUtils {

    private final Faker faker = Faker.instance();

    public static RestSignUpRequest restSignupRequest() {
        return new RestSignUpRequest()
                .country(faker.address().country())
                .state(faker.address().state())
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .username(faker.name().username())
                .email(faker.internet().emailAddress())
                .password(RandomStringUtils.random(10, true, true));
    }

    public static RestLoginRequest restLoginRequest(final RestSignUpRequest restSignupRequest) {
        return new RestLoginRequest()
                .email(restSignupRequest.getEmail())
                .password(restSignupRequest.getPassword());
    }

    public static RestRefreshTokenRequest restRefreshTokenRequest(final String refreshToken) {
        return new RestRefreshTokenRequest()
                .refreshToken(refreshToken);
    }

    public static RestUserModel restUserModel() {
        return new RestUserModel()
                .username(faker.name().username())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().cellPhone())
                .imageUrl(faker.internet().url())
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .country(faker.address().country())
                .city(faker.address().city())
                .timezone(faker.address().timeZone())
                .birthday(faker.date().birthday().toString());

    }
}
