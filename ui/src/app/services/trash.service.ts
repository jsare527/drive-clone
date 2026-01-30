import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FolderResponse } from '../models/folder';
import { PageResponse, TrashDTO } from '../models/trashDTO';

@Injectable({
  providedIn: 'root'
})
export class TrashService {

  constructor(private readonly http: HttpClient) { }

  baseUrl = '/api/folders';

  getTrash(page: number, size: number) {
    const params = new HttpParams()
    .set('page', page.toString())
    .set('size', size.toString());

    return this.http.get<PageResponse<TrashDTO>>(`${this.baseUrl}/trash`, { params });
  }
}
