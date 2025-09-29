package org.example.ecom.service;

import lombok.AllArgsConstructor;
import org.example.ecom.dto.RegisterAdminRequest;
import org.example.ecom.dto.AdminRequest;
import org.example.ecom.model.Admin;
import org.example.ecom.model._Role;
import org.example.ecom.model._User;
import org.example.ecom.repository.UserRepo;
import org.example.ecom.repository.AdminRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AdminService {
    private final AdminRepo adminRepo;
    private final StorageService storageService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public List<Admin> findAll() {
        return adminRepo.findAll();
    }

    public Optional<Admin> findById(Long id) {
        return adminRepo.findById(id);
    }

    public Admin saveAdmin(Admin admin) {
        return adminRepo.save(admin);
    }

    public void deleteAdminById(Long id) {
        adminRepo.deleteById(id);
    }

    public Admin updateAdmin(Long id, Admin admin, MultipartFile file) {
        storageService.store(file);
        admin.setImage(file.getOriginalFilename());

        Admin existingAdmin = adminRepo.findById(id).orElseThrow(() -> new RuntimeException("Admin not found"));
        existingAdmin.setUsername(admin.getUsername());
        existingAdmin.setAddress(admin.getAddress());
        existingAdmin.setPhone(admin.getPhone());
        existingAdmin.setEmail(admin.getEmail());
        existingAdmin.setAddress(admin.getAddress());
        existingAdmin.setPassword(admin.getPassword());
        existingAdmin.setImage(admin.getImage());
        return adminRepo.save(existingAdmin);
    }

    public void register(RegisterAdminRequest registerAdminRequest) {
        if (userRepo.findByUsername(registerAdminRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepo.findByEmail(registerAdminRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        Admin admin = Admin.builder()
                .fullName(registerAdminRequest.getFullName())
                .email(registerAdminRequest.getEmail())
                .username(registerAdminRequest.getUsername())
                .birthDate(registerAdminRequest.getBirthDate())
                .role(_Role.ROLE_VENDOR)
                .password(passwordEncoder.encode(registerAdminRequest.getPassword()))
                .isEnabled(true)
                .build();
        userRepo.save(admin);
    }

    public AdminRequest getAdminByUsername(String username) {
        _User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != _Role.ROLE_ADMIN) {
            throw new RuntimeException("User is not a admin");
        }

        Admin admin = adminRepo.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Admin details not found"));

        AdminRequest dto = new AdminRequest();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setFullName(user.getFullName());
        dto.setBirthDate(user.getBirthDate());
        dto.setImage(user.getImage());
        return dto;
    }
}
