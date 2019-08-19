package pl.dev.news.devnewsservice.utils;

import lombok.experimental.UtilityClass;
import org.junit.Assert;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.model.rest.RestSignUpRequest;

@UtilityClass
public class TestAsserts {
    public void assertUserEntityToSignUpRequest(
            final UserEntity userEntity,
            final RestSignUpRequest restSignUpRequest
    ) {
        Assert.assertNotNull(userEntity);
        Assert.assertNotNull(restSignUpRequest);
        Assert.assertEquals(userEntity.getLastName(), restSignUpRequest.getLastName());
        Assert.assertEquals(userEntity.getFirstName(), restSignUpRequest.getFirstName());
        Assert.assertEquals(userEntity.getEmail(), restSignUpRequest.getEmail());
    }
}
