package pl.dev.news.devnewsservice.repository;

import org.springframework.stereotype.Repository;
import pl.dev.news.devnewsservice.entity.CategoryEntity;
import pl.dev.news.devnewsservice.entity.QCategoryEntity;
import pl.dev.news.devnewsservice.repository.custom.CustomQueryDslRspository;

import java.util.UUID;

@Repository
public interface CategoryRepository extends CustomQueryDslRspository<CategoryEntity, QCategoryEntity, UUID> {

}
