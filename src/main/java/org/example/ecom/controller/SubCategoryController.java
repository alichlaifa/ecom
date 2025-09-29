package org.example.ecom.controller;

import lombok.AllArgsConstructor;
import org.example.ecom.model.Category;
import org.example.ecom.model.SubCategory;
import org.example.ecom.service.SubCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/subcategory")
public class SubCategoryController {
    private final SubCategoryService subCategoryService;

    @GetMapping
    public List<SubCategory> getSubCategory() {
        return subCategoryService.getSubCategory();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubCategory> getSubCategoryById(@PathVariable Long id) {
        return subCategoryService.getSubCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public SubCategory saveSubCategory(@ModelAttribute SubCategory subCategory, @RequestPart MultipartFile file) {
        return subCategoryService.saveSubCategory(subCategory, file);
    }

    @DeleteMapping("/{id}")
    public void deleteSubCategoryById(@PathVariable Long id) {
        subCategoryService.deleteSubCategoryById(id);
    }

    @PutMapping(value = "/{id}")
    public SubCategory updateSubCategory(@PathVariable Long id, @ModelAttribute SubCategory subCategory, @RequestPart(required = false) MultipartFile file) {
        return subCategoryService.updateSubCategory(id, subCategory, file);
    }

    @GetMapping("/{id}/category")
    public Category getCategory(@PathVariable Long id) {
        return subCategoryService.getCategoryBySubCategoryId(id);
    }

    @PostMapping("/byCategoryId")
    public SubCategory addSubcategoryByCategoryId(@RequestParam Long categoryId, @ModelAttribute SubCategory subCategory, @RequestPart MultipartFile file) {
        return subCategoryService.addSubcategoryByCategoryId(categoryId, subCategory, file);
    }

}
