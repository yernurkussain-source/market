package kz.project.mymarket.repositories;

import kz.project.mymarket.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    Long countProductsByCategoryId(@Param("categoryId") Long categoryId);
}
