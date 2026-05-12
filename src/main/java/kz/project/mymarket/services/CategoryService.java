package kz.project.mymarket.services;

import kz.project.mymarket.entities.Category;
import kz.project.mymarket.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class CategoryService {

    private final CategoryRepository categoryRepository;


    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public Category update(Long id, Category category) {
        Category existing = findById(id);
        existing.setName(category.getName());
        return categoryRepository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        Long productCount = categoryRepository.countProductsByCategoryId(id);
        if (productCount > 0) {
            throw new RuntimeException("This category contains products. Delete those products first or move them to another category.");
        }
        categoryRepository.deleteById(id);
    }


}