package pl.dev.news.devnewsservice.utils;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.StringPath;
import lombok.experimental.UtilityClass;

@UtilityClass
public class QueryUtils {

    public Predicate likeIfNotNull(final String str, final StringPath path) {
        if (str != null && str.length() > 0) {
            return path.like(str);
        }
        return null;
    }

}
