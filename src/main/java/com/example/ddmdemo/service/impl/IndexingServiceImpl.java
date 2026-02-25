package com.example.ddmdemo.service.impl;

import ai.djl.translate.TranslateException;
import com.example.ddmdemo.dto.ForensicReportDTO;
import com.example.ddmdemo.exceptionhandling.exception.LoadingException;
import com.example.ddmdemo.exceptionhandling.exception.StorageException;
import com.example.ddmdemo.indexmodel.ForensicReportIndex;
import com.example.ddmdemo.indexrepository.ForensicReportIndexRepository;
import com.example.ddmdemo.model.ForensicReport;
import com.example.ddmdemo.respository.ForensicReportRepository;
import com.example.ddmdemo.service.interfaces.FileService;
import com.example.ddmdemo.service.interfaces.IndexingService;
import com.example.ddmdemo.util.ForensicReportParser;
import com.example.ddmdemo.util.VectorizationUtil;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;
import org.elasticsearch.common.geo.GeoPoint;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexingServiceImpl implements IndexingService {

    private final ForensicReportIndexRepository forensicReportIndexRepository;
    private final ForensicReportRepository forensicReportRepository;
    private final FileService fileService;
    private final ForensicReportParser forensicReportParser;
    private final GeocodingServiceImpl geocodingService;

    @Override
    public ForensicReportDTO parseDocument(MultipartFile documentFile) {
        // Validacija mime tipa
        detectMimeType(documentFile);

        // Čuvanje u MinIO
        var serverFilename = fileService.store(documentFile, UUID.randomUUID().toString());

        // Čuvanje u PostgreSQL
        var report = new ForensicReport();
        report.setServerFilename(serverFilename);
        report.setMimeType("application/pdf");
        forensicReportRepository.save(report);

        // Parsiranje teksta
        var text = extractDocumentContent(documentFile);
        log.info("EXTRACTED TEXT:\n{}", text);
        var dto = forensicReportParser.parse(text);
        dto.setServerFilename(serverFilename);



        return dto;
    }

    @Override
    @Transactional
    public void indexDocument(ForensicReportDTO dto, String serverFilename) {
        var index = new ForensicReportIndex();
        index.setForensicAnalyst(dto.getForensicAnalyst());
        index.setOrganization(dto.getOrganization());
        index.setMalwareName(dto.getMalwareName());
        index.setDescription(dto.getDescription());
        index.setThreatClassification(dto.getThreatClassification());
        index.setHashValue(dto.getHashValue());
        if (dto.getAddress() != null && !dto.getAddress().isBlank()) {
            var coords = geocodingService.getCoordinates(dto.getAddress());
            if (coords != null) {
                log.info("GEOCODING SUCCESS address={} lat={} lng={}", dto.getAddress(), coords[0], coords[1]);
                index.setLocation(new GeoPoint(coords[0], coords[1]));
            } else {
                log.warn("GEOCODING FAILED address={}", dto.getAddress());
            }
        }
        index.setServerFilename(serverFilename);

        try {
            index.setVectorizedContent(
                    VectorizationUtil.getEmbedding(dto.getDescription()));
        } catch (TranslateException e) {
            log.error("Could not vectorize document: {}", serverFilename);
        }

        forensicReportIndexRepository.save(index);

        String city = "";
        if (dto.getAddress() != null && !dto.getAddress().isBlank()) {
            var parts = dto.getAddress().split(",");
            city = parts[parts.length - 1].trim();
        }

        log.info("REPORT_INDEXED SUCCESS organization={} city={} analyst={} malware={} classification={}",
                dto.getOrganization(),
                city,
                dto.getForensicAnalyst(),
                dto.getMalwareName(),
                dto.getThreatClassification());
    }

    private String extractDocumentContent(MultipartFile multipartPdfFile) {
        try (var pdfFile = multipartPdfFile.getInputStream()) {
            var pdDocument = PDDocument.load(pdfFile);
            var textStripper = new PDFTextStripper();
            var content = textStripper.getText(pdDocument);
            pdDocument.close();
            return content;
        } catch (IOException e) {
            throw new LoadingException("Error while trying to load PDF file content.");
        }
    }

    private void detectMimeType(MultipartFile file) {
        var contentAnalyzer = new Tika();
        try {
            var trueMimeType = contentAnalyzer.detect(file.getBytes());
            var specifiedMimeType = Files.probeContentType(
                    Path.of(Objects.requireNonNull(file.getOriginalFilename())));
            if (!trueMimeType.equals(specifiedMimeType) &&
                    !(trueMimeType.contains("zip") && specifiedMimeType.contains("zip"))) {
                throw new StorageException("True mime type is different from specified one.");
            }
        } catch (IOException e) {
            throw new StorageException("Failed to detect mime type for file.");
        }
    }
}