package org.example.ecom.model;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@NoArgsConstructor
@Data
public class Client extends _User {

}
