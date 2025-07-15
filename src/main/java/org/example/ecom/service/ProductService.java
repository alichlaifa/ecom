package org.example.ecom.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.ecom.model.Category;
import org.example.ecom.model.Client;
import org.example.ecom.model.Product;
import org.example.ecom.repository.CategoryRepo;
import org.example.ecom.repository.ClientRepo;
import org.example.ecom.repository.ProductRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ProductService {
    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final ClientRepo clientRepo;
    private final StorageService storageService;

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepo.findById(id);
    }

    public Product saveProduct(Product product) {
        return productRepo.save(product);
    }

    public void deleteProduct(Long id) {
        productRepo.deleteById(id);
    }

    public Product updateProduct(Long id, Product product, MultipartFile file) {
        storageService.store(file);
        product.setImage(file.getOriginalFilename());

        Product existingProduct = productRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("Product not found with id " + product.getId()));
        existingProduct.setColor(product.getColor());
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStock(product.getStock());
        existingProduct.setImage(product.getImage());
        existingProduct.setDescription(product.getDescription());
        return productRepo.save(existingProduct);
    }

    public Product addProductToCategory(Long categoryId, Product product) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategory(category);
        return productRepo.save(product);
    }

    public Product addProductByClientId(Long clientId, Product product) {
        Client client = clientRepo.findById(clientId).orElseThrow(() -> new RuntimeException("Client not found"));
        product.setClient(client);
        return productRepo.save(product);
    }

    public Product addProductByClientIdAndCategoryId(Long clientId, Long categoryId, Product product, MultipartFile file) {
        // Sauvegarder l'image via StorageService
        storageService.store(file);
        // Enregistrer le nom du fichier dans le produit
        product.setImage(file.getOriginalFilename());

        // Associer client et catégorie
        Client client = clientRepo.findById(clientId).orElseThrow(() -> new RuntimeException("Client not found"));
        Category category = categoryRepo.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
        product.setClient(client);
        product.setCategory(category);

        // Sauvegarder produit
        return productRepo.save(product);
    }

    public Product getProductByName(String name) {
        return productRepo.findByName(name).orElseThrow(() -> new RuntimeException("Product not found"));
    }
    
}
