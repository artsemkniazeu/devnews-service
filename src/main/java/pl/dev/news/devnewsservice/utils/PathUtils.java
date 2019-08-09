package pl.dev.news.devnewsservice.utils;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Pattern;

@UtilityClass
public class PathUtils {

    private static final Pattern variablePattern = Pattern.compile("\\{\\S+?}");

    public static String generate(final String url, final Object... variables) {
        final LinkedList<Object> replacements = new LinkedList<>(Arrays.asList(variables));
        return variablePattern.matcher(url).replaceAll(matchResult -> String.valueOf(replacements.poll()));
    }

}
