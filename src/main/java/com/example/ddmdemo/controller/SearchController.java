package com.example.ddmdemo.controller;

import com.example.ddmdemo.dto.SearchQueryDTO;
import com.example.ddmdemo.dto.SearchResultDTO;
import com.example.ddmdemo.indexmodel.ForensicReportIndex;
import com.example.ddmdemo.service.interfaces.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/simple")
    public Page<SearchResultDTO> simpleSearch(@RequestParam Boolean isKnn,
                                              @RequestBody SearchQueryDTO simpleSearchQuery,
                                              Pageable pageable) {
        return searchService.simpleSearch(simpleSearchQuery.keywords(), pageable, isKnn);
    }

    @PostMapping("/advanced")
    public Page<SearchResultDTO> advancedSearch(@RequestParam String expression,
                                                Pageable pageable) {
        return searchService.advancedSearch(expression, pageable);
    }
}