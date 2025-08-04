package org.example.ecom.controller;

import lombok.AllArgsConstructor;
import org.example.ecom.model.Product;
import org.example.ecom.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Optional<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @ModelAttribute Product product, @RequestPart MultipartFile file) {
        return productService.updateProduct(id, product, file);
    }

    @PostMapping("/byCategoryId/{categoryId}")
    public ResponseEntity<Product> addProductByCategoryId(@PathVariable Long categoryId, @RequestBody Product product) {
        Product savedProduct = productService.addProductToCategory(categoryId, product);
        return ResponseEntity.ok(savedProduct);
    }

    @PostMapping("/byClientId/{clientId}")
    public Product addProductToClient(@PathVariable Long clientId, @RequestBody Product product) {
        return productService.addProductByClientId(clientId, product);
    }

    @PostMapping("/byClientIdAndCategoryId/{clientId}")
    public Product addProductByClientIdAndCategoryId(@PathVariable Long clientId, @RequestParam Long categoryId, @ModelAttribute Product product,  @RequestPart MultipartFile file) {
        return productService.addProductByClientIdAndCategoryId(clientId,categoryId,product, file);
    }

    @GetMapping("/productByName")
    public Product getProductByName(@RequestParam String name) {
        return productService.getProductByName(name);
    }
}
