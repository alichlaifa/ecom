package org.example.ecom.controller;

import lombok.AllArgsConstructor;
import org.example.ecom.dto.RegisterAdminRequest;
import org.example.ecom.dto.SuccessMessageRequest;
import org.example.ecom.dto.AdminRequest;
import org.example.ecom.model.Admin;
import org.example.ecom.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/admin")
public class AdminController {
    private final AdminService AdminService;

    @GetMapping
    public List<Admin> findAll() {
        return AdminService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Admin> findById(@PathVariable Long id) {
        return AdminService.findById(id);
    }

    @PostMapping
    public Admin saveAdmin(@RequestBody Admin Admin) {
        return AdminService.saveAdmin(Admin);
    }

    @DeleteMapping("/{id}")
    public void deleteAdminById(@PathVariable Long id) {
        AdminService.deleteAdminById(id);
    }

    @PutMapping("/{id}")
    public Admin updateAdmin(@PathVariable Long id, @ModelAttribute Admin Admin, @RequestPart MultipartFile file) {
        return AdminService.updateAdmin(id,Admin, file);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterAdminRequest registerAdminRequest) {
        try {
            AdminService.register(registerAdminRequest);
            return ResponseEntity.ok(new SuccessMessageRequest("User registered successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<AdminRequest> getAdminByUsername(@PathVariable String username) {
        try {
            AdminRequest Admin = AdminService.getAdminByUsername(username);
            return ResponseEntity.ok(Admin);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }
}