package pl.dev.news.devnewsservice.utils;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class FileUtil {

    public String generateUniqueName(final String file) {
        return UUID.randomUUID().toString() + "-" + file;
    }

}
