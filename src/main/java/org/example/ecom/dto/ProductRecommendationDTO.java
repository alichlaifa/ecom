package org.example.ecom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductRecommendationDTO {
    private Long productId;
    private String name;
    private String image;
    private Double price;
    private Double score;
}
