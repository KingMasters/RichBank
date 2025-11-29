package com.hexagonal.domain.vo;

import lombok.Value;

@Value
public class Address {
    String street;
    String city;
    String state;
    String zipCode;
    String country;

    private Address(String street, String city, String state, String zipCode, String country) {
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street cannot be null or empty");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        if (zipCode == null || zipCode.isBlank()) {
            throw new IllegalArgumentException("Zip code cannot be null or empty");
        }
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("Country cannot be null or empty");
        }
        this.street = street.trim();
        this.city = city.trim();
        this.state = state != null ? state.trim() : null;
        this.zipCode = zipCode.trim();
        this.country = country.trim();
    }

    public static Address of(String street, String city, String state, String zipCode, String country) {
        return new Address(street, city, state, zipCode, country);
    }

    public static Address of(String street, String city, String zipCode, String country) {
        return new Address(street, city, null, zipCode, country);
    }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder(street);
        sb.append(", ").append(city);
        if (state != null && !state.isBlank()) {
            sb.append(", ").append(state);
        }
        sb.append(" ").append(zipCode);
        sb.append(", ").append(country);
        return sb.toString();
    }
}

