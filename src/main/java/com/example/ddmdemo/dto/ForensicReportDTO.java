package com.example.ddmdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForensicReportDTO {
    private String forensicAnalyst;
    private String organization;
    private String malwareName;
    private String description;
    private String threatClassification;
    private String hashValue;
    private String serverFilename;
    private String address;
}