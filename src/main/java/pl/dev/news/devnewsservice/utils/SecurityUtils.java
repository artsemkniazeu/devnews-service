package pl.dev.news.devnewsservice.utils;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.dev.news.devnewsservice.entity.UserEntity;

import java.util.UUID;

@UtilityClass
public class SecurityUtils {

    public static boolean isAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserEntity;
    }

    public static UUID getUserId() {
        final UserEntity userEntity = (UserEntity) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();
        return userEntity.getId();
    }

}
