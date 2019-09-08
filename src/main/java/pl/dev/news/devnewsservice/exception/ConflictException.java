package pl.dev.news.devnewsservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends BaseException {


    public ConflictException(final String message, final Object... variables) {
        super(message, variables);
    }
}
