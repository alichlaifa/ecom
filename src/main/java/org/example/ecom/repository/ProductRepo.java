package org.example.ecom.repository;

import org.example.ecom.model.Product;
import org.example.ecom.model.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductRepo extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.name = :name")
    Optional<Product> findByName(@Param("name") String name);

    @Query("SELECT p FROM Product p WHERE p.subCategory.id = :subCategoryId")
    List<Product> findBySubCategoryId(@Param("subCategoryId") Long subCategoryId);

    @Query("SELECT p FROM Product p WHERE p.subCategory.category.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.price > :minPrice and p.price < :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") Long minPrice, @Param("maxPrice") Long maxPrice);

    @Query(value = """
        SELECT p.id, p.name, 'product' as type, p.image, p.price,
               COALESCE(AVG(r.rating),0) + COUNT(f.id)*0.1 as score
        FROM product p
        LEFT JOIN review r ON r.product_id = p.id
        LEFT JOIN favorite f ON f.product_id = p.id
        WHERE MATCH(p.name, p.description) AGAINST (:query IN NATURAL LANGUAGE MODE)
        GROUP BY p.id
        ORDER BY score DESC
        LIMIT 20
        """, nativeQuery = true)
    List<Map<String,Object>> searchProductsWithScore(@Param("query") String query);

    @Query(value = "SELECT id, name, 'category' as type FROM category WHERE name LIKE CONCAT('%', :query, '%') LIMIT 5", nativeQuery = true)
    List<Map<String,Object>> searchCategories(@Param("query") String query);

    @Query(value = "SELECT id, name, 'subcategory' as type FROM sub_category WHERE name LIKE CONCAT('%', :query, '%') LIMIT 10", nativeQuery = true)
    List<Map<String,Object>> searchSubCategories(@Param("query") String query);
}
