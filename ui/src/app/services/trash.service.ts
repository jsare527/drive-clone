import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FolderResponse } from '../models/folder';
import { PageResponse, TrashDTO } from '../models/trashDTO';

@Injectable({
  providedIn: 'root'
})
export class TrashService {

  constructor(private readonly http: HttpClient) { }

  folderBaseUrl = '/api/folders';
  fileBaseUrl = '/api/files';

  getTrash(page: number, size: number) {
    const params = new HttpParams()
    .set('page', page.toString())
    .set('size', size.toString());

    return this.http.get<PageResponse<TrashDTO>>(`${this.folderBaseUrl}/trash`, { params });
  }

  deleteFolderForever(folderId: number) {
    return this.http.delete(`${this.folderBaseUrl}/folder/${folderId}`);
  }

  restoreFolder(folderId: number) {
    return this.http.get(`${this.folderBaseUrl}/restore/${folderId}`);
  }

  deleteFileForever(fileId: number) {
    return this.http.delete(`${this.fileBaseUrl}/file/${fileId}`);
  }

  restoreFile(fileId: number) {
    return this.http.get(`${this.fileBaseUrl}/restore/${fileId}`);
  }
}
