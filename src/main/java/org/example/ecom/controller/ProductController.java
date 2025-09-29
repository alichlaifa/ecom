package org.example.ecom.controller;

import lombok.AllArgsConstructor;
import org.example.ecom.model.Product;
import org.example.ecom.model.SubCategory;
import org.example.ecom.service.NotificationService;
import org.example.ecom.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final SimpMessagingTemplate messagingTemplate;
    private NotificationService notificationService;

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
        Product saved = productService.saveProduct(product);
        String message = "New product created: " + saved.getName();
        notificationService.saveNotification("PRODUCT_CREATED", message);
        messagingTemplate.convertAndSend("/topic/products", message);
        return saved;
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        String message = "Product deleted: " + product.getName() + " (ID " + product.getId() + ")";
        notificationService.saveNotification("PRODUCT_DELETED", message);
        messagingTemplate.convertAndSend("/topic/products", message);
        productService.deleteProduct(id);
    }


    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @ModelAttribute Product product, @RequestPart MultipartFile file) {
        return productService.updateProduct(id, product, file);
    }

    @PostMapping("/byCategoryId/{categoryId}")
    public ResponseEntity<Product> addProductBySubCategoryId(@PathVariable Long categoryId, @RequestBody Product product) {
        Product savedProduct = productService.addProductToSubCategory(categoryId, product);
        return ResponseEntity.ok(savedProduct);
    }

    @PostMapping("/byClientId/{vendorId}")
    public Product addProductToClient(@PathVariable Long vendorId, @RequestBody Product product) {
        return productService.addProductByClientId(vendorId, product);
    }

    @PostMapping("/byClientIdAndSubcategoryId/{vendorId}")
    public Product addProductByClientIdAndSubCategoryId(@PathVariable Long vendorId, @RequestParam Long subcategoryId, @ModelAttribute Product product,  @RequestPart MultipartFile file) {
        return productService.addProductByClientIdAndSubCategoryId(vendorId, subcategoryId, product, file);
    }

    @GetMapping("/productByName")
    public Product getProductByName(@RequestParam String name) {
        return productService.getProductByName(name);
    }

    @GetMapping("/{productId}/subcategory")
    public SubCategory getSubCategoryByProductId(@PathVariable Long productId) {
        return productService.getproductSubCategoryById(productId);
    }

    @GetMapping("/getProductForClientBySubCategoryId/{subcategoryId}")
    public List<Product> getProductForClientBySubCategoryId(@PathVariable Long subcategoryId) {
        return productService.getProductsForClientBySubCategoryId(subcategoryId);
    }

    @GetMapping("/getProductForClientByCategoryId/{categoryId}")
    public List<Product> getProductForClientByCategoryId(@PathVariable Long categoryId) {
        return productService.getProductsForClientByCategoryId(categoryId);
    }

    @GetMapping("/getProductForClientByPriceRange/{minPrice}/{maxPrice}")
    public List<Product> getProductForClientByPriceRange(@PathVariable Long minPrice, @PathVariable Long maxPrice) {
        return productService.getProductsForClientByPriceRange(minPrice, maxPrice);
    }
}
