package com.example.ddmdemo.service.interfaces;

import com.example.ddmdemo.indexmodel.ForensicReportIndex;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface SearchService {

    Page<ForensicReportIndex> simpleSearch(List<String> keywords, Pageable pageable, boolean isKNN);

    Page<ForensicReportIndex> advancedSearch(String expression, Pageable pageable);
}