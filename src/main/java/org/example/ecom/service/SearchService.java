package org.example.ecom.service;

import lombok.AllArgsConstructor;
import org.example.ecom.model.Product;
import org.example.ecom.repository.ProductRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class SearchService {
    private ProductRepo productRepo;

    public List<Map<String, Object>> searchAll(String query) {
        List<Map<String, Object>> results = new ArrayList<>();

        results.addAll(productRepo.searchProductsWithScore(query));
        results.addAll(productRepo.searchCategories(query));
        results.addAll(productRepo.searchSubCategories(query));
        return results;
    }
}
