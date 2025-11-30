package com.hexagonal.framework.adapter.output.persistence.h2.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressEmbeddable {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
}
