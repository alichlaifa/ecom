package org.example.ecom.controller;

import lombok.RequiredArgsConstructor;
import org.example.ecom.model.Product;
import org.example.ecom.repository.ProductRepo;
import org.example.ecom.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/autocomplete-products")
    public List<Map<String,Object>> autocomplete(@RequestParam String query){
        return searchService.searchAll(query);
    }

    @GetMapping
    public List<Map<String, Object>> search(@RequestParam String query) {
        return searchService.searchAll(query);
    }
}

