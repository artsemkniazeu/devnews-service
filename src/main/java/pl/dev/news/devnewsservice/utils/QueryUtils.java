package pl.dev.news.devnewsservice.utils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.StringPath;

public class QueryUtils {

    private final BooleanBuilder builder;

    public QueryUtils() {
        this.builder = new BooleanBuilder();
    }

    public Predicate build() {
        return builder.getValue();
    }

    public BooleanBuilder getBuilder() {
        return builder;
    }

    public QueryUtils andLikeAny(final String str, final StringPath... paths) {
        if (str != null && str.length() > 0) {
            final BooleanBuilder booleanBuilder = new BooleanBuilder();
            for (final StringPath path:paths) {
                booleanBuilder.or(path.contains(str));
            }
            builder.and(booleanBuilder);
        }
        return this;
    }

    public QueryUtils orLikeAny(final String str, final StringPath... paths) {
        if (str != null && str.length() > 0) {
            final BooleanBuilder booleanBuilder = new BooleanBuilder();
            for (final StringPath path:paths) {
                booleanBuilder.or(path.contains(str));
            }
            builder.or(booleanBuilder);
        }
        return this;
    }

    public QueryUtils andEq(final Object obj, final ComparablePath... paths) {
        if (obj != null) {
            for (final ComparablePath path:paths) {
                builder.and(path.eq(obj));
            }
        }
        return this;
    }

    public QueryUtils andEq(final String obj, final StringPath... paths) {
        if (obj != null) {
            for (final StringPath path:paths) {
                builder.and(path.eq(obj));
            }
        }
        return this;
    }

    public QueryUtils orEq(final String obj, final StringPath... paths) {
        if (obj != null) {
            for (final StringPath path:paths) {
                builder.or(path.eq(obj));
            }
        }
        return this;
    }

    public QueryUtils and(final BooleanExpression expression) {
        this.builder.and(expression);
        return this;
    }

    public QueryUtils or(final BooleanExpression expression) {
        this.builder.or(expression);
        return this;
    }

    public QueryUtils and(final BooleanBuilder builder) {
        this.builder.and(builder);
        return this;
    }

    public QueryUtils or(final BooleanBuilder builder) {
        this.builder.or(builder);
        return this;
    }


}
