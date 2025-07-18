package org.example.ecom.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class Vendor extends _User {
    private String companyName;
}
