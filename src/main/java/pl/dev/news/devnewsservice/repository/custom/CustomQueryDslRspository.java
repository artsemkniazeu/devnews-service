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
import java.util.UUID;

@NoRepositoryBean
public interface CustomQueryDslRspository<
        T extends AuditableEntity, P extends EntityPathBase<T>, I extends Serializable> extends JpaRepository<T, I>,
        QuerydslPredicateExecutor<T>, QuerydslBinderCustomizer<P> {



    @Override
    default void customize(final QuerydslBindings bindings, final P root){

    }

    default Predicate soft(final Predicate predicate) {
        final Type genericInterface = CustomQueryDslRspository.class.getGenericInterfaces()[0];
        final TypeVariable variable = (TypeVariable) ((ParameterizedType) genericInterface)
                .getActualTypeArguments()[0];
        final Class clazz = TypeFactory.rawClass(TypeUtils.getImplicitBounds(variable)[0]);
        //noinspection unchecked
        final PathBuilder<?> pathBuilder = new PathBuilder(clazz, "deletedAt");
        final Predicate p = pathBuilder.isNull();
        return ExpressionUtils.allOf(predicate, p);
    }

    @Transactional
    @Modifying
    @Query("update #{#entityName} e set deleted_at = CURRENT_TIMESTAMP where e.id = ?1")
    void softDelete(UUID id);

}
