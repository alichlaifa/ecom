package org.example.ecom.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.ecom.model.Category;
import org.example.ecom.repository.CategoryRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepo categoryRepo;
    private final  StorageService storageService;

    public List<Category> getCategory() {
        return categoryRepo.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepo.findById(id);
    }

    public Category saveCategory(Category category, MultipartFile file) {
        storageService.store(file);
        category.setImage(file.getOriginalFilename());

        return categoryRepo.save(category);
    }

    public void deleteCategoryById(Long id) {
        categoryRepo.deleteById(id);
    }

    public Category updateCategory(Long id, Category category, MultipartFile file) {
        Category existingCategory = categoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + id));

        // ✅ Update name if provided
        if (category.getName() != null && !category.getName().isBlank()) {
            existingCategory.setName(category.getName());
        }

        // ✅ Update image only if file is provided
        if (file != null && !file.isEmpty()) {
            storageService.store(file);
            existingCategory.setImage(file.getOriginalFilename());
        }

        return categoryRepo.save(existingCategory);
    }

}
