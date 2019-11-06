package pl.dev.news.devnewsservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.exception.NotFoundException;
import pl.dev.news.devnewsservice.repository.UserRepository;
import pl.dev.news.devnewsservice.utils.SecurityUtils;

import java.util.UUID;

import static pl.dev.news.devnewsservice.constants.ExceptionConstants.userWithIdNotFound;

@RequiredArgsConstructor
@Component("securityResolver")
public class SecurityResolver {

    private final UserRepository userRepository;

    public boolean isResourceOwner(final UUID userId) {
        return userId != null
                && SecurityUtils.isAuthenticated()
                && userId.equals(SecurityUtils.getUserId());
    }

    public UserEntity getUser() {
        final UUID userId = SecurityUtils.getUserId();
        return userRepository.softFindById(userId)
                .orElseThrow(() -> new NotFoundException(userWithIdNotFound, userId));
    }

}
