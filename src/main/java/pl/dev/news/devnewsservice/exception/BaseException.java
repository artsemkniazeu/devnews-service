package pl.dev.news.devnewsservice.exception;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Pattern;

public abstract class BaseException extends RuntimeException {

    private static final Pattern variablePattern = Pattern.compile("\\{}");

    public BaseException(final String message, final Object... variables) {
        super(generate(message, variables));
    }

    public BaseException(final String message, final Throwable throwable, final Object... variables) {
        super(generate(message, variables), throwable);
    }

    private static String generate(final String exception, final Object... variables) {
        final LinkedList<Object> replacements = new LinkedList<>(Arrays.asList(variables));
        return variablePattern.matcher(exception).replaceAll(matchResult -> String.valueOf(replacements.poll()));
    }

}
