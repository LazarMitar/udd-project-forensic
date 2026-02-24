package com.example.ddmdemo.dto;

import com.example.ddmdemo.indexmodel.ForensicReportIndex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDTO {
    private ForensicReportIndex document;
    private Map<String, List<String>> highlights;
}