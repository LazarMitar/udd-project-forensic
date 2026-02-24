package com.example.ddmdemo.service.interfaces;

import com.example.ddmdemo.dto.SearchResultDTO;
import com.example.ddmdemo.indexmodel.ForensicReportIndex;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface SearchService {
    Page<SearchResultDTO> simpleSearch(List<String> keywords, Pageable pageable, boolean isKNN);
    Page<SearchResultDTO> advancedSearch(String expression, Pageable pageable);
    Page<SearchResultDTO> geoSearch(String address, double radiusKm, Pageable pageable);
}