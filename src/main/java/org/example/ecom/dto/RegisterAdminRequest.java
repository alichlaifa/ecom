package org.example.ecom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterAdminRequest {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private LocalDate birthDate;
}
