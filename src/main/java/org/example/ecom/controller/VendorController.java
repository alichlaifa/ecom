package org.example.ecom.controller;

import lombok.AllArgsConstructor;
import org.example.ecom.model.Vendor;
import org.example.ecom.service.VendorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/vendor")
public class VendorController {
    private final VendorService vendorService;

    @GetMapping
    public List<Vendor> findAll() {
        return vendorService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Vendor> findById(@PathVariable Long id) {
        return vendorService.findById(id);
    }

    @PostMapping
    public Vendor saveVendor(@RequestBody Vendor vendor) {
        return vendorService.saveVendor(vendor);
    }

    @DeleteMapping("/{id}")
    public void deleteVendorById(@PathVariable Long id) {
        vendorService.deleteVendorById(id);
    }

    @PutMapping("/{id}")
    public Vendor updateVendor(@PathVariable Long id, @RequestBody Vendor vendor) {
        return vendorService.updateVendor(id,vendor);
    }
}
