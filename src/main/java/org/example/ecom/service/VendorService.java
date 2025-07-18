package org.example.ecom.service;

import lombok.AllArgsConstructor;
import org.example.ecom.dto.RegisterRequest;
import org.example.ecom.dto.RegisterVendorRequest;
import org.example.ecom.model.Client;
import org.example.ecom.model.Vendor;
import org.example.ecom.model._Role;
import org.example.ecom.repository.UserRepo;
import org.example.ecom.repository.VendorRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class VendorService {
    private final VendorRepo vendorRepo;
    private final StorageService storageService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public List<Vendor> findAll() {
        return vendorRepo.findAll();
    }

    public Optional<Vendor> findById(Long id) {
        return vendorRepo.findById(id);
    }

    public Vendor saveVendor(Vendor vendor) {
        return vendorRepo.save(vendor);
    }

    public void deleteVendorById(Long id) {
        vendorRepo.deleteById(id);
    }

    public Vendor updateVendor(Long id, Vendor vendor, MultipartFile file) {
        storageService.store(file);
        vendor.setImage(file.getOriginalFilename());

        Vendor existingVendor = vendorRepo.findById(id).orElseThrow(() -> new RuntimeException("Vendor not found"));
        existingVendor.setUsername(vendor.getUsername());
        existingVendor.setAddress(vendor.getAddress());
        existingVendor.setPhone(vendor.getPhone());
        existingVendor.setEmail(vendor.getEmail());
        existingVendor.setAddress(vendor.getAddress());
        existingVendor.setPassword(vendor.getPassword());
        existingVendor.setImage(vendor.getImage());
        return vendorRepo.save(existingVendor);
    }

    public void register(RegisterVendorRequest registerVendorRequest) {
        if (userRepo.findByUsername(registerVendorRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepo.findByEmail(registerVendorRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        Vendor vendor = Vendor.builder()
                .fullName(registerVendorRequest.getFullName())
                .email(registerVendorRequest.getEmail())
                .username(registerVendorRequest.getUsername())
                .birthDate(registerVendorRequest.getBirthDate())
                .role(_Role.ROLE_VENDOR)
                .password(passwordEncoder.encode(registerVendorRequest.getPassword()))
                .isEnabled(true)
                .companyName(registerVendorRequest.getCompanyName())
                .build();
        userRepo.save(vendor);
    }
}
