import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { IndexService } from '../../../core/services/index.service';
import { ForensicReportDTO } from '../../../core/models/forensic-report-dto.model';

@Component({
  selector: 'app-report-upload',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './report-upload.component.html',
  styleUrl: './report-upload.component.scss'
})
export class ReportUploadComponent {
  private indexService = inject(IndexService);
  private fb = inject(FormBuilder);
  private router = inject(Router);

  form = this.fb.nonNullable.group({
    forensicAnalyst: [''],
    organization: [''],
    malwareName: [''],
    description: [''],
    threatClassification: [''],
    hashValue: [''],
    serverFilename: ['']
  });

  parsedDto: ForensicReportDTO | null = null;
  loading = false;
  error = '';
  success = false;

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;
    if (file.type !== 'application/pdf') {
      this.error = 'Izaberite PDF fajl.';
      return;
    }
    this.error = '';
    this.success = false;
    this.parsedDto = null;
    this.loading = true;
    this.indexService.parseDocument(file).subscribe({
      next: dto => {
        this.parsedDto = dto;
        this.form.patchValue({
          forensicAnalyst: dto.forensicAnalyst ?? '',
          organization: dto.organization ?? '',
          malwareName: dto.malwareName ?? '',
          description: dto.description ?? '',
          threatClassification: dto.threatClassification ?? '',
          hashValue: dto.hashValue ?? '',
          serverFilename: dto.serverFilename ?? ''
        });
        this.loading = false;
      },
      error: err => {
        this.loading = false;
        this.error = err.error?.message || 'Greška pri parsiranju dokumenta.';
      }
    });
    input.value = '';
  }

  confirm(): void {
    if (!this.parsedDto?.serverFilename) return;
    this.error = '';
    this.loading = true;
    const dto: ForensicReportDTO = {
      ...this.form.getRawValue(),
      serverFilename: this.parsedDto.serverFilename
    };
    this.indexService.confirmIndexing(dto).subscribe({
      next: () => {
        this.loading = false;
        this.success = true;
        this.parsedDto = null;
        this.form.reset();
      },
      error: err => {
        this.loading = false;
        this.error = err.error?.message || 'Greška pri indeksiranju.';
      }
    });
  }

  cancel(): void {
    this.parsedDto = null;
    this.form.reset();
    this.error = '';
    this.router.navigate(['/dashboard']);
  }
}
