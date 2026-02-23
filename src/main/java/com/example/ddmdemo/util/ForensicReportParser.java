package com.example.ddmdemo.util;

import com.example.ddmdemo.dto.ForensicReportDTO;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ForensicReportParser {

    public ForensicReportDTO parse(String text) {
        var dto = new ForensicReportDTO();

        dto.setOrganization(extract(text, "Organizacija\\s+([^\\n<]+)"));
        dto.setMalwareName(extract(text, "([^\\n]+)\\s*Klasifikacija:"));
        dto.setThreatClassification(extract(text, "Klasifikacija:\\s*([^,\\n]+)"));
        dto.setHashValue(extract(text, "Klasifikacija:[^,]+,\\s*([A-Fa-f0-9]{32,64})"));
        dto.setDescription(extract(text, "Opis ponašanja malvera/pretnje:\\s*([\\s\\S]+?)(?=IME|$)"));
        dto.setForensicAnalyst(extract(text, "Potpisforenzičara\\s*([^\\n]+)"));

        return dto;
    }

    private String extract(String text, String pattern) {
        var matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(text);
        return matcher.find() ? matcher.group(1).trim() : "";
    }
}