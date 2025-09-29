package org.example.ecom.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.ecom.model.*;
import org.example.ecom.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ProductService {
    private final ProductRepo productRepo;
    private final VendorRepo vendorRepo;
    private final StorageService storageService;
    private final SubCategoryRepo subcategoryRepo;

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
        existingProduct.setSubCategory(product.getSubCategory());
        return productRepo.save(existingProduct);
    }

    public Product addProductToSubCategory(Long subcategoryId, Product product) {
          SubCategory subcategory = subcategoryRepo.findById(subcategoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setSubCategory(subcategory);
        return productRepo.save(product);
    }

    public Product addProductByClientId(Long vendorId, Product product) {
//        Client client = clientRepo.findById(clientId).orElseThrow(() -> new RuntimeException("Client not found"));
//        product.setClient(client);
            Vendor vendor = vendorRepo.findById(vendorId).orElseThrow(() -> new RuntimeException("Vendor not found"));
            product.setVendor(vendor);
        return productRepo.save(product);
    }

    public Product addProductByClientIdAndSubCategoryId(Long vendorId, Long subcategoryId, Product product, MultipartFile file) {
        // Store the uploaded file and set the image filename on the product
        storageService.store(file);
        product.setImage(file.getOriginalFilename());

        // Retrieve related Client and Category entities from the database
        Vendor vendor = vendorRepo.findById(vendorId).orElseThrow(() -> new RuntimeException("vendor not found"));
        //Category category = categoryRepo.findById(subcategoryId).orElseThrow(() -> new RuntimeException("Category not found"));
        SubCategory subcategory = subcategoryRepo.findById(subcategoryId).orElseThrow(() -> new RuntimeException("Category not found"));
        product.setVendor(vendor);
        product.setSubCategory(subcategory);

        return productRepo.save(product);
    }

    public Product getProductByName(String name) {
        return productRepo.findByName(name).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public SubCategory getproductSubCategoryById(Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        SubCategory subCategory = product.getSubCategory(); // assuming the field is named this
        if (subCategory == null) {
            throw new RuntimeException("Subcategory not found for this product");
        }
        return subCategory;
    }

    public List<Product> getProductsForClientBySubCategoryId(Long subCategoryID) {
        return productRepo.findBySubCategoryId(subCategoryID);
    }

    public List<Product> getProductsForClientByCategoryId(Long CategoryID) {
        return productRepo.findByCategoryId(CategoryID);
    }

    public List<Product> getProductsForClientByPriceRange(Long minPrice, Long maxPrice) {
        return productRepo.findByPriceRange(minPrice, maxPrice);
    }

}
