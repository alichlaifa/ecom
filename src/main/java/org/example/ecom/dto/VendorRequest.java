package org.example.ecom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorRequest {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String address;
    private String companyName;
    private String image;
    private String fullName;
    private LocalDate birthDate;
}
