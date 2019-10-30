package pl.dev.news.devnewsservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class UnprocessableEntityException extends BaseException {

    public UnprocessableEntityException(final String message, final Object... variables) {
        super(message, variables);
    }

    public UnprocessableEntityException(final String message, final Throwable throwable, final Object... variables) {
        super(message, throwable, variables);
    }
}
