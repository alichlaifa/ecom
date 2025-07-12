package org.example.ecom.controller;

import lombok.AllArgsConstructor;
import org.example.ecom.model.Category;
import org.example.ecom.service.CategoryService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<Category> getCategory() {
        return categoryService.getCategory();
    }

    @GetMapping("/{id}")
    public Optional<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @PostMapping
    public Category saveCategory(@ModelAttribute Category category, @RequestPart MultipartFile file) {
        return categoryService.saveCategory(category, file);
    }

    @DeleteMapping("/{id}")
    public void deleteCategoryById(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
    }

    @PutMapping(value = "/{id}")
    public Category updateCategory(@PathVariable Long id, @ModelAttribute Category category, @RequestPart(required = false) MultipartFile file) {
        return categoryService.updateCategory(id, category, file);
    }
}
