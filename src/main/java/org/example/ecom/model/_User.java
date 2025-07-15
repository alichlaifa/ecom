package org.example.ecom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
public class _User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;
    private String address;
    private String phone;
    private String image;
    private String fullName;
    private LocalDateTime birthDate;
    private Boolean isEnabled;

    @Enumerated(value = EnumType.STRING)
    private _Role role;

    // Renvoie la liste des rôles ou permissions de l'utilisateur.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Renvoie true si les informations d'identification (mot de passe, etc.) ne sont pas expirées.
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    // Permet de stocker un token d’authentification lié à un utilisateur.
    @OneToOne(mappedBy = "user")
    private _Token token;
}
