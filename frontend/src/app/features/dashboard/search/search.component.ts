import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { SearchService } from '../../../core/services/search.service';
import { PageResponse, SearchResult } from '../../../core/models/search-result.model';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './search.component.html',
  styleUrl: './search.component.scss'
})
export class SearchComponent {
  private fb = inject(FormBuilder);
  private searchService = inject(SearchService);

  form = this.fb.nonNullable.group({
    query: [''],
    useKnn: [false],
    advancedExpression: ['']
  });

  results: SearchResult[] = [];
  page?: PageResponse<SearchResult>;
  loading = false;
  error = '';

  currentPage = 0;
  readonly pageSize = 10;

  onSubmit(): void {
    if (this.form.invalid || this.loading) return;
    this.currentPage = 0;
    this.executeSearch();
  }

  goToPage(page: number): void {
    if (page < 0 || (this.page && page >= this.page.totalPages)) return;
    this.currentPage = page;
    this.executeSearch();
  }

  private executeSearch(): void {
    this.error = '';
    this.loading = true;
    this.results = [];

    const { query, useKnn, advancedExpression } = this.form.getRawValue();

    const source$ =
      advancedExpression && advancedExpression.trim().length > 0
        ? this.searchService.advancedSearch(advancedExpression.trim(), this.currentPage, this.pageSize)
        : this.searchService.simpleSearch(query.trim(), !!useKnn, this.currentPage, this.pageSize);

    source$.subscribe({
      next: page => {
        this.page = page;
        this.results = page.content;
        this.loading = false;
      },
      error: err => {
        this.loading = false;
        this.page = undefined;
        this.results = [];
        this.error =
          err?.error && typeof err.error === 'string'
            ? err.error
            : 'Greška pri pretrazi. Pokušajte ponovo.';
      }
    });
  }

  getHighlight(result: SearchResult): string | null {
    const h = result.highlights;
    return (
      h?.['description']?.[0] ||
      h?.['malware_name']?.[0] ||
      h?.['organization']?.[0] ||
      h?.['forensic_analyst']?.[0] ||
      null
    );
  }
}

