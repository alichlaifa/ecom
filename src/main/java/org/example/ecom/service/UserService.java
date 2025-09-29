package org.example.ecom.service;

import lombok.AllArgsConstructor;
import org.example.ecom.dto.*;
import org.example.ecom.model.*;
import org.example.ecom.repository.TokenRepo;
import org.example.ecom.repository.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final TokenRepo tokenRepo;
    private final StorageService storageService;

    public void registerClient(RegisterClientRequest request) {
        checkDuplicateEmailOrUsername(request.getEmail(), request.getUsername());

        Client client = Client.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .username(request.getUsername())
                .birthDate(request.getBirthDate())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(_Role.ROLE_CLIENT)
                .isEnabled(true)
                .build();

        userRepo.save(client);
    }

    public void registerVendor(RegisterVendorRequest request) {
        checkDuplicateEmailOrUsername(request.getEmail(), request.getUsername());

        Vendor vendor = Vendor.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .username(request.getUsername())
                .birthDate(request.getBirthDate())
                .companyName(request.getCompanyName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(_Role.ROLE_VENDOR)
                .isEnabled(true)
                .build();

        userRepo.save(vendor);
    }

    private void checkDuplicateEmailOrUsername(String email, String username) {
        if (userRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("This email is already in use");
        }
        if (userRepo.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("This username has been taken");
        }
    }

    public JwtToken login(LoginRequest loginRequest) {
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));
            return new JwtToken(createToken(auth));
        } catch (DisabledException e) {
            throw new IllegalArgumentException("The account has been disabled");
        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("Incorrect credentials");
        } catch (Exception e) {
            throw new RuntimeException("Incorrect credentials");
        }
    }

    public List<_User> getAllUsers() {
        return userRepo.findAll();
    }

    public _User getUserById(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public _User getUserByUsername(String username) {
        return userRepo.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Username not found"));
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

    private String createToken(Authentication authentication) {
        var claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60 * 300))
                .subject(authentication.getName())
                .claim("scope", createScope(authentication))
                .build();

        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(claims);
        return jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
    }

    private String createScope(Authentication authentication) {
        return authentication.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.joining(" "));
    }

    private String generateToken() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private Date getExpirationDate() {
        return new Date(System.currentTimeMillis() + 1000 * 60 * 10);
    }

    public void resetPassword(ResetPasswordRequest request) {
        _User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("There is no user with this email!"));
        _Token token = tokenRepo.findByUserEmail(request.getEmail());

        if (request.getNewPassword().equals(request.getConfirmPassword()) &&
                request.getTokenText().equals(token.getTokenText())) {

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            tokenRepo.deleteById(token.getIdToken());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong");
        }
    }

    public void sendToken(String email) {
        Optional<_User> user = userRepo.findByEmail(email);
        if (user.isPresent()) {
            String tokenText = this.generateToken();
            _Token existingToken = tokenRepo.findByUserEmail(email);
            if (existingToken != null) {
                tokenRepo.deleteById(existingToken.getIdToken());
            }
            _Token tokenToSave = new _Token();
            tokenToSave.setTokenText(tokenText);
            tokenToSave.setExpiryDate(this.getExpirationDate());
            tokenToSave.setUser(user.get());
            _Token token = tokenRepo.save(tokenToSave);
        }
    }

    public void verifiedToken(VerifiedTokenRequest request) {
        Date dateNow = new Date(System.currentTimeMillis());
        _Token token = tokenRepo.findByUserEmail(request.getEmail());
        Date expDateCurrentToken = token.getExpiryDate();
        int result = dateNow.compareTo(expDateCurrentToken);
        if (result > 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The link has expired.");
        }
        if (!request.getTokenText().equals(token.getTokenText())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong token provided");
        }
    }

    public void changePassword(_User user, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);
    }

    public void deleteUserByUsername(String username) {
        _User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepo.delete(user);
    }

    public void disableUser(Long id) {
        _User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsEnabled(false);
        userRepo.save(user);
    }

    public void enableUser(Long id) {
        _User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsEnabled(true);
        userRepo.save(user);
    }
}
