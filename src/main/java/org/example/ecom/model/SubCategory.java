package org.example.ecom.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class SubCategory {
    @Id
    private Long id;
    private String name;
    private String image;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
