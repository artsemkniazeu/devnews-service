package pl.dev.news.devnewsservice.repository.custom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@NoRepositoryBean
public interface SoftJpaRepository<T, I> extends JpaRepository<T, I> {
    @Transactional
    @Modifying
    @Query("update #{#entityName} e set deleted_at = CURRENT_TIMESTAMP where e.id = ?1")
    void softDelete(UUID id);
}
