package com.example.ddmdemo.controller;

import com.example.ddmdemo.dto.ForensicReportDTO;
import com.example.ddmdemo.service.interfaces.IndexingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/index")
@RequiredArgsConstructor
public class IndexController {

    private final IndexingService indexingService;

    // Faza 1 - upload i parsiranje
    @PostMapping("/parse")
    @ResponseStatus(HttpStatus.OK)
    public ForensicReportDTO parseDocument(@RequestParam("file") MultipartFile file) {
        return indexingService.parseDocument(file);
    }

    // Faza 2 - potvrda indeksiranja
    @PostMapping("/confirm")
    @ResponseStatus(HttpStatus.CREATED)
    public void confirmIndexing(@RequestBody ForensicReportDTO dto) {
        indexingService.indexDocument(dto, dto.getServerFilename());
    }
}