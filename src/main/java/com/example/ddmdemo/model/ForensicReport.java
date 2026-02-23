package com.example.ddmdemo.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "forensic_report")
public class ForensicReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "server_filename")
    private String serverFilename;

    @Column(name = "mime_type")
    private String mimeType;
}