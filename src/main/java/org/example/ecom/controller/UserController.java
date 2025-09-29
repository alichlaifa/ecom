package org.example.ecom.controller;

import org.example.ecom.dto.*;
import org.example.ecom.model._User;
import org.example.ecom.repository.UserRepo;
import org.example.ecom.service.NotificationService;
import org.example.ecom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;
    private SimpMessagingTemplate messagingTemplate;
    private NotificationService notificationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        System.out.println("Received login request: " + loginRequest);
        try{
            JwtToken jwtToken =userService.login(loginRequest);
            return ResponseEntity.ok(jwtToken);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/register/client")
    public ResponseEntity<?> registerClient(@RequestBody RegisterClientRequest request) {
        try {
            userService.registerClient(request);
            String message = "New client registered: " + request.getUsername();
            notificationService.saveNotification("USER_REGISTERED", message);
            messagingTemplate.convertAndSend("/topic/users", message);
            return ResponseEntity.ok(new SuccessMessageRequest("Client registered successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/register/vendor")
    public ResponseEntity<?> registerVendor(@RequestBody RegisterVendorRequest request) {
        try {
            userService.registerVendor(request);
            String message = "New vendor registered: " + request.getUsername();
            notificationService.saveNotification("USER_REGISTERED", message);
            messagingTemplate.convertAndSend("/topic/users", message);
            return ResponseEntity.ok(new SuccessMessageRequest("Vendor registered successfully!"));
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

    @PostMapping("/auth/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            _User user = userRepo.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            userService.changePassword(user, request);
            return ResponseEntity.ok(new SuccessMessageRequest("Password changed successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(@RequestParam String username) {
        try {
            userService.deleteUserByUsername(username);
            String message = "User deleted: " + username;
            notificationService.saveNotification("USER_DELETED", message);
            messagingTemplate.convertAndSend("/topic/users", message);
            return ResponseEntity.ok("Account deleted successfully ✅");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<?> disableUser(@PathVariable Long id) {
        try {
            userService.disableUser(id);
//            String message = "User disabled: " + id;
//            notificationService.saveNotification("USER_DISABLED", message);
//            messagingTemplate.convertAndSend("/topic/users", message);
            return ResponseEntity.ok("User disabled successfully 🚫");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/enable")
    public ResponseEntity<?> enableUser(@PathVariable Long id) {
        try {
            userService.enableUser(id);
//            String message = "User enabled: " + id;
//            notificationService.saveNotification("USER_ENABLED", message);
//            messagingTemplate.convertAndSend("/topic/users", message);
            return ResponseEntity.ok("User enabled successfully ✅");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
