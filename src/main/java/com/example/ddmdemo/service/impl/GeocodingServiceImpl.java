package com.example.ddmdemo.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class GeocodingServiceImpl {

    @Value("${opencage.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public double[] getCoordinates(String address) {
        var sanitized = sanitizeAddress(address);
        var url = "https://api.opencagedata.com/geocode/v1/json?q="
                + sanitized.replace(" ", "+")
                + "&key=" + apiKey;

        try {
            var response = restTemplate.getForObject(url, java.util.Map.class);
            var results = (java.util.List<?>) response.get("results");
            if (results == null || results.isEmpty()) return null;

            var geometry = (java.util.Map<?, ?>) ((java.util.Map<?, ?>) results.get(0)).get("geometry");
            double lat = ((Number) geometry.get("lat")).doubleValue();
            double lng = ((Number) geometry.get("lng")).doubleValue();
            return new double[]{lat, lng};
        } catch (Exception e) {
            log.error("Geocoding failed for address: {}", sanitized);
            return null;
        }
    }

    private String sanitizeAddress(String address) {
        var parts = address.split(",");
        if (parts.length <= 2) return address;
        return parts[0].trim() + ", " + parts[parts.length - 1].trim();
    }
}