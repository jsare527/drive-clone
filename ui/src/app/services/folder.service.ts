import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FolderResponse } from '../models/folder';

@Injectable({
  providedIn: 'root'
})
export class FolderService {

  constructor(private readonly http: HttpClient) { }
  private baseUrl = '/api/folders';

  uploadFolder(formData: FormData) {
    return this.http.post(`${this.baseUrl}/uploadFolder`, formData);
  }

  getFolder(folderId: number) {
    if (folderId === 0) return this.http.get<FolderResponse>(`${this.baseUrl}`);
    return this.http.get<FolderResponse>(`${this.baseUrl}?folderId=${folderId}`);
  }

  downloadFolder(folderId: number) {
    return this.http.get(`${this.baseUrl}/downloadFolder?folderId=${folderId}`, {
      responseType: 'blob',
      reportProgress: true,
      observe: 'events'
    });
  }

  deleteFolder(folderId: number) {
    const formData = new FormData();
    formData.append('folderId', folderId.toString());
    return this.http.post(`${this.baseUrl}/deleteFolder`, formData);
  }
}
