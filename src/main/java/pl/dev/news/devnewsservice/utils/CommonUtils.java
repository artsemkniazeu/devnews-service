package pl.dev.news.devnewsservice.utils;

public class CommonUtils {

    public static <T> String nullSafeToString(final T from) {
        return from == null ? "" : from.toString();
    }

}
