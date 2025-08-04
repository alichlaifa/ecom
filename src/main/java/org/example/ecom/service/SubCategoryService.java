package org.example.ecom.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.ecom.model.Category;
import org.example.ecom.model.SubCategory;
import org.example.ecom.repository.SubCategoryRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SubCategoryService {
    private final SubCategoryRepo subCategoryRepo;
    private final StorageService storageService;

    public List<SubCategory> getSubCategory() {
        return subCategoryRepo.findAll();
    }

    public Optional<SubCategory> getSubCategoryById(Long id) {
        return subCategoryRepo.findById(id);
    }

    public SubCategory saveSubCategory(SubCategory subCategory, MultipartFile file) {
        storageService.store(file);
        subCategory.setImage(file.getOriginalFilename());

        return subCategoryRepo.save(subCategory);
    }

    public void deleteSubCategoryById(Long id) {
        subCategoryRepo.deleteById(id);
    }

    public SubCategory updateSubCategory(Long id, SubCategory subCategory, MultipartFile file) {
        SubCategory existingSubCategory = subCategoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SubCategory not found with id " + id));

        if (subCategory.getName() != null && !subCategory.getName().isBlank()) {
            existingSubCategory.setName(subCategory.getName());
        }
        if (file != null && !file.isEmpty()) {
            storageService.store(file);
            existingSubCategory.setImage(file.getOriginalFilename());
        }
        return subCategoryRepo.save(existingSubCategory);
    }

    public Category getCategoryBySubCategoryId(Long subCategoryId) {
        SubCategory subCategory = subCategoryRepo.findById(subCategoryId)
                .orElseThrow(() -> new EntityNotFoundException("SubCategory not found with id " + subCategoryId));

        return subCategory.getCategory();
    }


}
