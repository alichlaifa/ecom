package org.example.ecom.controller;

import org.example.ecom.dto.*;
import org.example.ecom.model._User;
import org.example.ecom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try{
            JwtToken jwtToken =userService.login(loginRequest);
            return ResponseEntity.ok(jwtToken);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            userService.register(registerRequest);
            return ResponseEntity.ok(new SuccessMessageRequest("User registered successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<_User>> getAllUsers() {
        List<_User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<_User> getUserById(@PathVariable Long id) {
        try {
            _User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            _User user = userService.getUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }


    @PostMapping("/auth/send-token")
    public ResponseEntity<?> sendToken(@RequestParam String email) {
        try {
            userService.sendToken(email);
            return ResponseEntity.ok(new SuccessMessageRequest("Token sent successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/auth/verify-token")
    public ResponseEntity<?> verifiedToken(@RequestBody VerifiedTokenRequest verifTokenRequest) {
        try {
            userService.verifiedToken(verifTokenRequest);
            return ResponseEntity.ok(new SuccessMessageRequest("Token verified successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            userService.resetPassword(resetPasswordRequest);
            return ResponseEntity.ok(new SuccessMessageRequest("Password reset successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
