package org.example.ecom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchResultDTO {
    private Long id;
    private String name;
    private String type;
    private String image;
    private Double price;
    private Double score;
}
