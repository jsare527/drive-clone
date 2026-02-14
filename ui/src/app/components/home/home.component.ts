import { Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { FileService } from '../../services/file.service';
import { debounceTime, distinctUntilChanged, from, Subject, Subscription, switchMap } from 'rxjs';
import { FileDTO, FolderDTO, FolderResponse } from '../../models/folder';
import { MatIcon } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatMenu, MatMenuTrigger, MatMenuItem } from "@angular/material/menu";
import { FolderService } from '../../services/folder.service';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import { HttpEventType } from '@angular/common/http';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatFormFieldModule } from "@angular/material/form-field";
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [MatIcon, MatButtonModule, MatMenu, MatMenuTrigger, MatMenuItem, MatProgressBarModule, MatFormFieldModule, ReactiveFormsModule, MatInputModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {

  constructor(private fileService: FileService, private folderService: FolderService) {}
  private destroyRef = inject(DestroyRef);
  private searchSubject = new Subject<string>();
  searchControl = new FormControl('');

  response: FolderResponse | null = null;
  path: any[] = [];
  imageTypes: string[] = ['image/png', 'image/jpeg', 'image/gif', 'image/svg+xml'];
  selectedFolder: FolderDTO | null = null;
  selectedFile: FileDTO | null = null;
  isLoading: boolean = false;
  downloadProgress: number = 0;

  ngOnInit(): void {
    this.searchControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(term => {
        const currentFolderId = this.getCurrentFolderId();
        if (term && term.trim()) {
          return this.folderService.searchFolder(currentFolderId, term);
        } else {
          return this.folderService.getFolder(currentFolderId);
        }
      }),
      takeUntilDestroyed(this.destroyRef)
    )
    .subscribe(data => this.response = data);

    this.folderService.getFolder(0)
    .pipe(takeUntilDestroyed(this.destroyRef))
    .subscribe(data => this.response = data);

    this.searchSubject.next('');
  }

  onFileSelected(event: any) {
    const files: FileList = event.target.files;
    if (files) this.uploadFiles(files);
  }

  onFolderSelected(event: any) {
    const files: FileList = event.target.files;
    if (files) this.uploadFolder(files);
  }

  uploadFiles(files: FileList) {
    const currentFolderId =  this.getCurrentFolderId();
    const formData = new FormData();
    formData.append('folderId', currentFolderId);

    Array.from(files).forEach(file => {
      formData.append('files', file, file.name);
    });

    this.fileService.uploadFiles(formData)
    .pipe(takeUntilDestroyed(this.destroyRef))
    .subscribe({
      next: () => this.openFolder(currentFolderId),
      error: (err) => console.log(err)
    })
  }

  uploadFolder(files: FileList) {
    const currentFolderId =  this.getCurrentFolderId();
    const formData: any = new FormData();
    formData.append('folderId', currentFolderId.toString());

    Array.from(files).forEach(file => {
      formData.append('files', file, file.name);
      formData.append('relativePaths', file.webkitRelativePath);
    });
    
    this.folderService.uploadFolder(formData)
    .pipe(takeUntilDestroyed(this.destroyRef))
    .subscribe({
      next: () => this.openFolder(currentFolderId),
      error: (err) => console.log(err)
    })
  }

  openFolder(folderId: number, folderName?: string, index?: number) {
    if (this.isLoading) return;
    this.isLoading = true;
    this.searchControl.setValue('');

    this.folderService.getFolder(folderId)
    .pipe(takeUntilDestroyed(this.destroyRef))
    .subscribe({
      next: (response) => {
        this.response = response;
        if (folderId === 0) this.path = [];
        if (index !== undefined) this.path = this.path.slice(0, index + 1);
        if (folderName) this.path.push({id: folderId, name: folderName});

        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
      }
    })
  }

  onFileDelete() {
    if (this.selectedFile === null) return;
    const currentFolderId = this.getCurrentFolderId();
    this.fileService.deleteFile(this.selectedFile.id)
    .pipe(takeUntilDestroyed(this.destroyRef))
    .subscribe({
      next: () => {
        this.selectedFile = null;
        this.openFolder(currentFolderId);
      },
      error: (err) => {
        this.selectedFile = null;
        console.log(err);
      }
    })
  }

  onFolderDelete() {
    if (this.selectedFolder === null) return;
    const currentFolderId = this.getCurrentFolderId();
    this.folderService.deleteFolder(this.selectedFolder.id)
    .pipe(takeUntilDestroyed(this.destroyRef))
    .subscribe({
      next: () => {
        this.selectedFolder = null;
        this.openFolder(currentFolderId);
      },
      error: (err) => {
        this.selectedFolder = null;
      }
    })
  }

  handleFileMore(event: Event, file: FileDTO) {
    event.stopPropagation();
    this.selectedFile = file;
  }

  handleFolderMore(event: Event, folder: FolderDTO) {
    event.stopPropagation();
    this.selectedFolder = folder;
  }

  getCurrentFolderId() {
    return this.path.length > 0 ? this.path[this.path.length - 1].id : 0;
  }

  downloadFolder() {
    if (this.selectedFolder === null) return;
    this.isLoading = true;
    this.downloadProgress = 0;

    this.folderService.downloadFolder(this.selectedFolder.id)
    .pipe(takeUntilDestroyed(this.destroyRef))
    .subscribe({
      next: (response) => {
        if (response.type === HttpEventType.DownloadProgress) {
          this.downloadProgress = Math.round(100 * response.loaded / (response.total || response.loaded));
          console.log(this.downloadProgress);
        } else if (response.type === HttpEventType.Response) {
          this.saveFile(response.body, `${this.selectedFolder?.name}.zip`)
          this.isLoading = false;
          this.downloadProgress = 0;
        }
      }
    })
  }

  downloadFile() {
    if (this.selectedFile === null) return;
    this.isLoading = true;

    this.fileService.downloadFile(this.selectedFile.id)
    .pipe(takeUntilDestroyed(this.destroyRef))
    .subscribe({
      next: (response) => {
        if (response.type === HttpEventType.DownloadProgress) {
          this.downloadProgress = Math.round(100 * response.loaded / (response.total || response.loaded));
        } else if (response.type === HttpEventType.Response) {
          this.saveFile(response.body, `${this.selectedFile?.fileName}`);
          this.isLoading = false;
          this.downloadProgress = 0;
        }
      }
    })
  }

  private saveFile(blob: Blob | null, fileName: string) {
    if (!blob) return;
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = fileName;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    a.remove();
  }

}
