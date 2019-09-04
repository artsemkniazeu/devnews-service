package pl.dev.news.devnewsservice.utils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.StringPath;

public class QueryUtils {

    private final BooleanBuilder builder;

    public QueryUtils() {
        this.builder = new BooleanBuilder();
    }

    public Predicate build() {
        return builder.getValue();
    }

    public QueryUtils like(final String str, final StringPath path) {
        if (str != null && str.length() > 0) {
            builder.and(path.like(str));
        }
        return this;
    }

    public QueryUtils likeOr(final String str, final StringPath path) {
        if (str != null && str.length() > 0) {
            builder.or(path.like(str));
        }
        return this;
    }

    public QueryUtils likeOr(final String str, final StringPath... paths) {
        final BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (str != null && str.length() > 0) {
            for (final StringPath path:paths) {
                booleanBuilder.or(path.like(str));
            }
            builder.and(booleanBuilder);
        }
        return this;
    }

    public QueryUtils and(final Predicate right) {
        builder.and(right);
        return this;
    }

    public QueryUtils andAnyOf(final Predicate... args) {
        builder.andAnyOf(args);
        return this;
    }

    public QueryUtils andNot(final Predicate right) {
        builder.andNot(right);
        return this;
    }

    public QueryUtils not() {
        builder.not();
        return this;
    }

    public QueryUtils or(final Predicate right) {
        builder.or(right);
        return this;
    }

    public QueryUtils orAllOf(final Predicate... args) {
        builder.orAllOf(args);
        return this;
    }

    public QueryUtils orNot(final Predicate right) {
        builder.orNot(right);
        return this;
    }

}
