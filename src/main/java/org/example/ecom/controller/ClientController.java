package org.example.ecom.controller;

import lombok.AllArgsConstructor;
import org.example.ecom.dto.RegisterClientRequest;
import org.example.ecom.dto.SuccessMessageRequest;
import org.example.ecom.model.Client;
import org.example.ecom.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/client")
public class ClientController {
    private ClientService clientService;

    @GetMapping("/all")
    public List<Client> findAll() {
        return clientService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Client> findById(@PathVariable Long id) {
        return clientService.findById(id);
    }

    @PostMapping
    public Client saveClient(@RequestBody Client client) {
        return clientService.saveClient(client);
    }

    @DeleteMapping("/{id}")
    public void deleteClientById(@PathVariable Long id) {
        clientService.deleteClientById(id);
    }

    @PutMapping("/{id}")
    public Client updateClient(@PathVariable Long id, @ModelAttribute Client client, @RequestPart MultipartFile file) {
        return clientService.updateClient(id, client, file);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterClientRequest registerClientRequest) {
        try {
            clientService.register(registerClientRequest);
            return ResponseEntity.ok(new SuccessMessageRequest("User registered successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
