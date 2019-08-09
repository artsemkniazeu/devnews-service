package pl.dev.news.devnewsservice.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class TokenAuthenticationProvider implements AuthenticationProvider {

    private final TokenProvider tokenProvider;
    private final TokenValidator tokenValidator;

    @Override
    public Authentication authenticate(final Authentication authentication) {
        final TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;
        final String accessToken = tokenAuthentication.getName();
        if (tokenValidator.validateAccessToken(accessToken)) {
            final UserDetails userDetails = new UserDetailsImpl(tokenProvider.buildUserEntityByToken(accessToken));

            tokenAuthentication.setUserDetails(userDetails);
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
