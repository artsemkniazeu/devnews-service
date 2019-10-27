package pl.dev.news.devnewsservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LOCKED)
public class BadCredentialsException extends BaseException {

    public BadCredentialsException(final String message, final Object... variables) {
        super(message, variables);
    }
}
