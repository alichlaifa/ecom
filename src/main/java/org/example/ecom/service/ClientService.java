package org.example.ecom.service;

import lombok.AllArgsConstructor;
import org.example.ecom.dto.RegisterClientRequest;
import org.example.ecom.model.Client;
import org.example.ecom.model._Role;
import org.example.ecom.repository.ClientRepo;
import org.example.ecom.repository.UserRepo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ClientService {
    private final ClientRepo clientRepo;
    private final StorageService storageService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    public List<Client> findAll() {
        return clientRepo.findAll();
    }

    public Optional<Client> findById(Long id) {
        return clientRepo.findById(id);
    }

    public Client saveClient(Client client) {
        return clientRepo.save(client);
    }

    public void deleteClientById(Long id) {
        clientRepo.deleteById(id);
    }

    public Client updateClient(Long id, Client client, MultipartFile file) {
        storageService.store(file);
        client.setImage(file.getOriginalFilename());

        Client existingClient = clientRepo.findById(id).orElseThrow(() -> new RuntimeException("Client not found"));
        existingClient.setUsername(client.getUsername());
        existingClient.setPassword(client.getPassword());
        existingClient.setEmail(client.getEmail());
        existingClient.setPhone(client.getPhone());
        existingClient.setAddress(client.getAddress());
        existingClient.setRole(client.getRole());
        existingClient.setImage(client.getImage());
        return clientRepo.save(existingClient);
    }

    public void register(RegisterClientRequest registerClientRequest) {
        if (userRepo.findByUsername(registerClientRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepo.findByEmail(registerClientRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        Client client = Client.builder()
                .fullName(registerClientRequest.getFullName())
                .email(registerClientRequest.getEmail())
                .username(registerClientRequest.getUsername())
                .birthDate(registerClientRequest.getBirthDate())
                .role(_Role.ROLE_CLIENT)
                .password(passwordEncoder.encode(registerClientRequest.getPassword()))
                .isEnabled(true)
                .build();
        userRepo.save(client);
    }
}
