import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FolderResponse } from '../models/folder';

@Injectable({
  providedIn: 'root'
})
export class FileService {

  constructor(private readonly http: HttpClient) { }

  private baseUrl = '/api/files'

  uploadFiles(formData: FormData) {
    return this.http.post(`${this.baseUrl}/uploadFiles`, formData);
  }

  deleteFile(fileId: number) {
    const formData = new FormData();
    formData.append('fileId', fileId.toString());
    return this.http.post(`${this.baseUrl}/deleteFile`, formData);
  }

  downloadFile(fileId: number) {
    return this.http.get(`${this.baseUrl}/downloadFile?fileId=${fileId}`, {
      responseType: 'blob',
      reportProgress: true,
      observe: 'events' });
  }
}
