package org.example.ecom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class _Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idToken;
    private String tokenText;
    private Date expiryDate;

    @OneToOne
    @JoinColumn(name = "id_user")
    private _User user;
}
