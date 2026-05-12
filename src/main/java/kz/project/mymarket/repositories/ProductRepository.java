package kz.project.mymarket.repositories;

import kz.project.mymarket.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.product.id = :productId")
    Long countOrderItemsByProductId(@Param("productId") Long productId);
}
