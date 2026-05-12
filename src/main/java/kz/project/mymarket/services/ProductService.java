package kz.project.mymarket.services;

import kz.project.mymarket.dto.ProductDTO;
import kz.project.mymarket.entities.Category;
import kz.project.mymarket.entities.Product;
import kz.project.mymarket.repositories.CategoryRepository;
import kz.project.mymarket.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<ProductDTO> findAll() {
        return productRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO findById(Long id) {
        return toDTO(getById(id));
    }

    public ProductDTO save(ProductDTO dto) {
        validateProduct(dto);
        Product product = toEntity(dto);
        return toDTO(productRepository.save(product));
    }

    public ProductDTO update(Long id, ProductDTO dto) {
        validateProduct(dto);
        Product existing = getById(id);
        existing.setName(dto.getName());
        existing.setPrice(dto.getPrice());
        existing.setQuantity(dto.getQuantity());
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found: " + dto.getCategoryId()));
            existing.setCategory(category);
        }
        return toDTO(productRepository.save(existing));
    }

    public void delete(Long id) {
        getById(id);
        Long orderItems = productRepository.countOrderItemsByProductId(id);
        if (orderItems > 0) {
            throw new IllegalArgumentException("This product is used in orders. It cannot be deleted because order history must be preserved.");
        }
        productRepository.deleteById(id);
    }

    public Product getEntityById(Long id) {
        return getById(id);
    }

    private Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    public ProductDTO toDTO(Product p) {
        ProductDTO dto = new ProductDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setPrice(p.getPrice());
        dto.setQuantity(p.getQuantity());
        if (p.getCategory() != null) {
            dto.setCategoryId(p.getCategory().getId());
            dto.setCategoryName(p.getCategory().getName());
        }
        return dto;
    }

    private Product toEntity(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found: " + dto.getCategoryId()));
            product.setCategory(category);
        }
        return product;
    }

    private void validateProduct(ProductDTO dto) {
        if (dto.getPrice() == null || dto.getPrice() <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0");
        }
        if (dto.getQuantity() == null || dto.getQuantity() < 0) {
            throw new IllegalArgumentException("Product quantity cannot be negative");
        }
    }
}
