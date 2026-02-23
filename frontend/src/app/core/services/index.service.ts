import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ForensicReportDTO } from '../models/forensic-report-dto.model';

@Injectable({ providedIn: 'root' })
export class IndexService {
  private readonly api = `${environment.apiUrl}/api/index`;

  constructor(private http: HttpClient) {}

  parseDocument(file: File): Observable<ForensicReportDTO> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ForensicReportDTO>(`${this.api}/parse`, formData, {
      withCredentials: true
    });
  }

  confirmIndexing(dto: ForensicReportDTO): Observable<void> {
    return this.http.post<void>(`${this.api}/confirm`, dto, {
      withCredentials: true
    });
  }
}
