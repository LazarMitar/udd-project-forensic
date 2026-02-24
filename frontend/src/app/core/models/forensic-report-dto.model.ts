export interface ForensicReportDTO {
  forensicAnalyst: string;
  organization: string;
  malwareName: string;
  description: string;
  threatClassification: string;
  hashValue: string;
  serverFilename: string;
  address?: string;
}
