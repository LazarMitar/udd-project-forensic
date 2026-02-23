package com.example.ddmdemo.service.interfaces;

import com.example.ddmdemo.dto.ForensicReportDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface IndexingService {

    ForensicReportDTO parseDocument(MultipartFile documentFile);

    void indexDocument(ForensicReportDTO dto, String serverFilename);
}