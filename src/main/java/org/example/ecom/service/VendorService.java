package org.example.ecom.service;

import lombok.AllArgsConstructor;
import org.example.ecom.model.Vendor;
import org.example.ecom.repository.VendorRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class VendorService {
    private final VendorRepo vendorRepo;

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

    public Vendor updateVendor(Long id, Vendor vendor) {
        Vendor existingVendor = vendorRepo.findById(id).orElseThrow(() -> new RuntimeException("Vendor not found"));
        existingVendor.setUsername(vendor.getUsername());
        existingVendor.setAddress(vendor.getAddress());
        existingVendor.setPhone(vendor.getPhone());
        existingVendor.setEmail(vendor.getEmail());
        existingVendor.setAddress(vendor.getAddress());
        existingVendor.setPassword(vendor.getPassword());
        return vendorRepo.save(existingVendor);
    }
}
