package kz.project.mymarket.repositories;

import kz.project.mymarket.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {
}
