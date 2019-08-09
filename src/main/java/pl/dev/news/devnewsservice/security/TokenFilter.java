package pl.dev.news.devnewsservice.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@AllArgsConstructor
public class TokenFilter implements Filter {

    private final TokenValidator tokenValidator;

    @Override
    public void doFilter(
            final ServletRequest request,
            final ServletResponse response,
            final FilterChain chain
    ) throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final String token = httpServletRequest.getHeader(AUTHORIZATION);
        final TokenAuthentication tokenAuthentication = new TokenAuthentication(token);
        if (token != null && tokenValidator.validateAccessToken(token)) {
            SecurityContextHolder.getContext().setAuthentication(tokenAuthentication);
        }
        chain.doFilter(request, response);
    }
}
