package com.example.ddmdemo.controller;

import com.example.ddmdemo.dto.GeoPointDTO;
import com.example.ddmdemo.service.impl.GeocodingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/geo")
@RequiredArgsConstructor
public class GeoController {

    private final GeocodingServiceImpl geocodingService;

    @GetMapping("/coords")
    public ResponseEntity<?> getCoordinates(@RequestParam String address) {
        var coords = geocodingService.getCoordinates(address);
        if (coords == null) {
            return ResponseEntity.badRequest().body("Nije moguće dobiti koordinate za zadatu adresu.");
        }
        return ResponseEntity.ok(new GeoPointDTO(coords[0], coords[1]));
    }
}

