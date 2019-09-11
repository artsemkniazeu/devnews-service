package pl.dev.news.devnewsservice.repository.custom;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;
import pl.dev.news.devnewsservice.entity.AuditableEntity;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface CustomQueryDslRspository<
        T extends AuditableEntity, P extends EntityPathBase<T>, I extends Serializable> extends JpaRepository<T, I>,
        QuerydslPredicateExecutor<T>, QuerydslBinderCustomizer<P> {
    
    @Override
    default void customize(final QuerydslBindings bindings, final P root){

    }

    default Class getClazz() {
        final Type genericInterface = CustomQueryDslRspository.class.getGenericInterfaces()[0];
        final TypeVariable variable = (TypeVariable) ((ParameterizedType) genericInterface)
                .getActualTypeArguments()[0];
        return TypeFactory.rawClass(TypeUtils.getImplicitBounds(variable)[0]);
    }

    default Predicate soft(final Predicate predicate) {
        final Class clazz = getClazz();
        //noinspection unchecked
        final PathBuilder<AuditableEntity> pathBuilder = new PathBuilder(clazz, "deletedAt");
        final Predicate p = pathBuilder.isNull();
        return ExpressionUtils.allOf(predicate, p);
    }

    default Optional<T> softFindById(final I id) {
        if (id == null) {
            return Optional.empty();
        }
        final Class clazz = getClazz();
        //noinspection unchecked
        final PathBuilder<I> pathBuilder = new PathBuilder(clazz, "id");
        final Predicate predicate = pathBuilder.eq(id);
        return findOne(soft(predicate));
    }

    default boolean softExistsById(final I id) {
        if (id == null) {
            return false;
        }
        final Class clazz = getClazz();
        //noinspection unchecked
        final PathBuilder<I> pathBuilder = new PathBuilder(clazz, "id");
        final Predicate predicate = pathBuilder.eq(id);
        return exists(soft(predicate));
    }

    @Transactional
    @Modifying
    @Query("update #{#entityName} e set deleted_at = CURRENT_TIMESTAMP where e.id = ?1")
    void softDeleteById(UUID id);

}
