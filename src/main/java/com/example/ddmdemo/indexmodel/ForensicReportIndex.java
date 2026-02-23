package com.example.ddmdemo.indexmodel;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.elasticsearch.common.geo.GeoPoint;
import org.springframework.data.elasticsearch.annotations.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "forensic_reports")
@Setting(settingPath = "/configuration/serbian-analyzer-config.json")
public class ForensicReportIndex {

    @Id
    private String id;

    @Field(type = FieldType.Text, store = true, name = "forensic_analyst",
            analyzer = "serbian_simple", searchAnalyzer = "serbian_simple")
    private String forensicAnalyst;

    @Field(type = FieldType.Text, store = true, name = "organization",
            analyzer = "serbian_simple", searchAnalyzer = "serbian_simple")
    private String organization;

    @Field(type = FieldType.Keyword, store = true, name = "threat_classification")
    private String threatClassification;

    @Field(type = FieldType.Keyword, store = true, name = "hash_value")
    private String hashValue;

    @Field(type = FieldType.Text, store = true, name = "malware_name",
            analyzer = "serbian_simple", searchAnalyzer = "serbian_simple")
    private String malwareName;

    @Field(type = FieldType.Text, store = true, name = "description",
            analyzer = "serbian_simple", searchAnalyzer = "serbian_simple")
    private String description;

    @GeoPointField
    private GeoPoint location;

    @Field(type = FieldType.Dense_Vector, dims = 384, similarity = "cosine")
    private float[] vectorizedContent;

    @Field(type = FieldType.Integer, store = true, name = "database_id")
    private Integer databaseId;

    @Field(type = FieldType.Text, store = true, name = "server_filename", index = false)
    private String serverFilename;
}