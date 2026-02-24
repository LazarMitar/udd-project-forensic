import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PageResponse, SearchResult } from '../models/search-result.model';

@Injectable({ providedIn: 'root' })
export class SearchService {
  private readonly api = `${environment.apiUrl}/api/search`;

  constructor(private http: HttpClient) {}

  simpleSearch(
    query: string,
    isKnn: boolean,
    page: number,
    size: number
  ): Observable<PageResponse<SearchResult>> {
    const keywords = query
      .split(/\s+/)
      .map(k => k.trim())
      .filter(k => k.length > 0);

    const body = { keywords };

    let params = new HttpParams()
      .set('isKnn', String(isKnn))
      .set('page', String(page))
      .set('size', String(size));

    return this.http.post<PageResponse<SearchResult>>(`${this.api}/simple`, body, {
      params,
      withCredentials: true
    });
  }

  advancedSearch(
    expression: string,
    page: number,
    size: number
  ): Observable<PageResponse<SearchResult>> {
    let params = new HttpParams()
      .set('expression', expression)
      .set('page', String(page))
      .set('size', String(size));

    // telo nije bitno za backend, šaljemo prazan objekat
    return this.http.post<PageResponse<SearchResult>>(`${this.api}/advanced`, {}, {
      params,
      withCredentials: true
    });
  }

  getGeoCoords(
    address: string
  ): Observable<{ lat: number; lon: number }> {
    const params = new HttpParams().set('address', address);
    return this.http.get<{ lat: number; lon: number }>(`${environment.apiUrl}/api/geo/coords`, {
      params,
      withCredentials: true
    });
  }

  geoSearch(
    address: string,
    radiusKm: number,
    page: number,
    size: number
  ): Observable<PageResponse<SearchResult>> {
    let params = new HttpParams()
      .set('address', address)
      .set('radiusKm', String(radiusKm))
      .set('page', String(page))
      .set('size', String(size));

    return this.http.get<PageResponse<SearchResult>>(`${this.api}/geo`, {
      params,
      withCredentials: true
    });
  }
}

