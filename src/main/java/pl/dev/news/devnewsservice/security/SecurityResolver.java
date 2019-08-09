package pl.dev.news.devnewsservice.security;

import org.springframework.stereotype.Component;
import pl.dev.news.devnewsservice.utils.SecurityUtils;

import java.util.UUID;

@Component("securityResolver")
public class SecurityResolver {

    public boolean isResourceOwner(final UUID userId) {
        return userId != null
                && SecurityUtils.isAuthenticated()
                && userId.equals(SecurityUtils.getUserId());
    }

}
