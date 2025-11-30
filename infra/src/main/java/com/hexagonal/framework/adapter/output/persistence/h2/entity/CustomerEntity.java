package com.hexagonal.framework.adapter.output.persistence.h2.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    private String phoneNumber;
    
    @Embedded
    private AddressEmbeddable address;
    
    @Column(nullable = false)
    private Boolean active;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Current hashed password
    private String password;

    // Password history stored in a separate collection table
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "customer_password_history", joinColumns = @JoinColumn(name = "customer_id"))
    @Column(name = "password_hash")
    private List<String> passwordHistory;
}
