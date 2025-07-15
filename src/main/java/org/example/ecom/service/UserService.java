package org.example.ecom.service;

import lombok.AllArgsConstructor;
import org.example.ecom.model._User;
import org.example.ecom.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepo userRepo;

    public List<_User> getAllUsers() {
        return userRepo.findAll();
    }

    public Optional<_User> getUserById(Long id) {
        return userRepo.findById(id);
    }

    public _User saveUser(_User user) {
        return userRepo.save(user);
    }

    public void deleteUserById(Long id) {
        userRepo.deleteById(id);
    }

    public _User updateUser(Long id, _User user) {
        _User existingUser = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User with ID " + id + " not found"));
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        existingUser.setRole(user.getRole());
        existingUser.setAddress(user.getAddress());
        existingUser.setPhone(user.getPhone());

        return userRepo.save(existingUser);
    }



}
