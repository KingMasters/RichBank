package com.hexagonal.domain.vo;

import lombok.Value;

import java.net.URL;
import java.time.LocalDateTime;

@Value
public class TrackingInformation {
    String trackingNumber;
    String carrier;
    URL trackingUrl;
    LocalDateTime shippedAt;

    private TrackingInformation(String trackingNumber, String carrier, URL trackingUrl, LocalDateTime shippedAt) {
        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new IllegalArgumentException("Tracking number cannot be null or empty");
        }
        if (carrier == null || carrier.isBlank()) {
            throw new IllegalArgumentException("Carrier cannot be null or empty");
        }
        if (shippedAt == null) {
            throw new IllegalArgumentException("Shipped at cannot be null");
        }

        this.trackingNumber = trackingNumber.trim();
        this.carrier = carrier.trim();
        this.trackingUrl = trackingUrl;
        this.shippedAt = shippedAt;
    }

    public static TrackingInformation of(String trackingNumber, String carrier, LocalDateTime shippedAt) {
        return new TrackingInformation(trackingNumber, carrier, null, shippedAt);
    }

    public static TrackingInformation of(String trackingNumber, String carrier, URL trackingUrl, LocalDateTime shippedAt) {
        return new TrackingInformation(trackingNumber, carrier, trackingUrl, shippedAt);
    }

    public static TrackingInformation of(String trackingNumber, String carrier, String trackingUrl, LocalDateTime shippedAt) {
        try {
            URL url = trackingUrl != null && !trackingUrl.isBlank() 
                    ? new java.net.URI(trackingUrl).toURL() 
                    : null;
            return new TrackingInformation(trackingNumber, carrier, url, shippedAt);
        } catch (java.net.MalformedURLException | java.net.URISyntaxException e) {
            throw new IllegalArgumentException("Invalid tracking URL: " + trackingUrl, e);
        }
    }
}

