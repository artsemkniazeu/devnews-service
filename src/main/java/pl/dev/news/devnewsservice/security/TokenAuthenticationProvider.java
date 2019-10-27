package pl.dev.news.devnewsservice.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pl.dev.news.devnewsservice.entity.UserEntity;
import pl.dev.news.devnewsservice.exception.BadCredentialsException;
import pl.dev.news.devnewsservice.exception.NotFoundException;
import pl.dev.news.devnewsservice.repository.UserRepository;

import java.util.UUID;

import static pl.dev.news.devnewsservice.constants.ExceptionConstants.userWithIdIsLocked;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.userWithIdIsNotEnabled;
import static pl.dev.news.devnewsservice.constants.ExceptionConstants.userWithIdNotFound;

@Slf4j
@Component
@AllArgsConstructor
public class TokenAuthenticationProvider implements AuthenticationProvider {

    private final TokenProvider tokenProvider;
    private final TokenValidator tokenValidator;
    private final UserRepository userRepository;


    @Override
    public Authentication authenticate(final Authentication authentication) {
        final TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;
        final String accessToken = tokenAuthentication.getName();
        if (tokenValidator.validateAccessToken(accessToken)) {
            final UUID userId = tokenProvider.buildUserEntityByToken(accessToken).getId();
            final UserEntity user = userRepository.softFindById(userId)
                    .orElseThrow(() -> new NotFoundException(userWithIdNotFound, userId));
            if (!user.isEnabled()) {
                throw new BadCredentialsException(userWithIdIsNotEnabled, userId);
            }
            if (user.isLocked()) {
                throw new BadCredentialsException(userWithIdIsLocked, userId);
            }
            tokenAuthentication.setUserDetails(user);
            tokenAuthentication.setAuthenticated(true);
        } else {
            tokenAuthentication.setAuthenticated(false);
        }
        return tokenAuthentication;
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return TokenAuthentication.class.equals(authentication);
    }
}
