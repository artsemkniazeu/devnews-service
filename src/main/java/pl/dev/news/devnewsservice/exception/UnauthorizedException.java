package pl.dev.news.devnewsservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends BaseException {

    public UnauthorizedException(final String message, final Object... variables) {
        super(message, variables);
    }
}
