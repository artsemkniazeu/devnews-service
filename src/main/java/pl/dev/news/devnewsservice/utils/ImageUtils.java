package pl.dev.news.devnewsservice.utils;

import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@UtilityClass
public class ImageUtils {

    private static final String[] contentTypes = {
            "image/png",
            "image/jpg",
            "image/jpeg",
            "image/bmp",
            "image/gif",
            "image/ief",
            "image/pipeg",
            "image/svg+xml",
            "image/tiff"
    };

    public boolean isValid(final MultipartFile value) {
        return isSupportedContentType(value.getContentType());
    }

    private boolean isSupportedContentType(final String contentType) {
        return Arrays.stream(contentTypes)
                .anyMatch(type -> type.equals(contentType));
    }
}
