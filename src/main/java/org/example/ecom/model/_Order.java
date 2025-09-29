package org.example.ecom.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "_order",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "totalPrice", "status"}
        )
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class _Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String transactionId = UUID.randomUUID().toString();

    private String city;
    private String country;
    private String state;
    private Date orderDate;
    private Double totalPrice;

    @ManyToOne
    private _User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;
}
