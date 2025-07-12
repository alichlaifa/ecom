package org.example.ecom.service;

import lombok.AllArgsConstructor;
import org.example.ecom.model.Client;
import org.example.ecom.repository.ClientRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ClientService {
    private ClientRepo clientRepo;

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

    public Client updateClient(Long id, Client client) {
        Client existingClient = clientRepo.findById(id).orElseThrow(() -> new RuntimeException("Client not found"));
        existingClient.setUsername(client.getUsername());
        existingClient.setPassword(client.getPassword());
        existingClient.setEmail(client.getEmail());
        existingClient.setPhone(client.getPhone());
        existingClient.setAddress(client.getAddress());
        existingClient.setRole(client.getRole());
        return clientRepo.save(existingClient);
    }
}
