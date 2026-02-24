export interface ForensicReportIndexDoc {
  id: string;
  forensicAnalyst: string;
  organization: string;
  malwareName: string;
  description: string;
  threatClassification: string;
  hashValue: string;
  serverFilename: string;
}

export interface SearchResult {
  document: ForensicReportIndexDoc;
  highlights: Record<string, string[]>;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

