import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { FileDTO, FolderDTO, FolderResponse } from '../../models/folder';
import { FileService } from '../../services/file.service';
import { FolderService } from '../../services/folder.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpEventType } from '@angular/common/http';
import { FileExplorerComponent } from "../file-explorer/file-explorer.component";

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [FileExplorerComponent],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss'
})
export class MainComponent implements OnInit {
  response: FolderResponse | null = null;
  path: any[] = [];
  isLoading = false;
  downloadProgress = 0;
  selectedFile: FileDTO | null = null;
  selectedFolder: FolderDTO | null = null;
  destroyRef = inject(DestroyRef);

  constructor(private fileService: FileService, private folderService: FolderService) {}

  ngOnInit(): void {
    this.loadFolder(0);
  }

  loadFolder(folderId: number, fileName?: string, index?: number) {
    this.folderService.getFolder(folderId)
    .pipe(takeUntilDestroyed(this.destroyRef))
    .subscribe(data => {
      this.response = data;
      if (index !== undefined) this.path = this.path.slice(0, index + 1);
      if (fileName) this.path.push({id: data.currentFolder.id, name: data.currentFolder.name});
    });
  }

  onFolderOpened(eventData: any[]) {
    const [target, index, fileName] = eventData;

    if (target === 0) {
      this.loadFolder(0);
      this.path = [];
    } else if (index !== undefined) {
      this.loadFolder(target, undefined, index);
    } else if (fileName !== undefined) {
      this.loadFolder(target, fileName);
    }
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
      next: () => this.loadFolder(currentFolderId),
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
      next: () => this.loadFolder(currentFolderId),
      error: (err) => console.log(err)
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
        this.loadFolder(currentFolderId);
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
        this.loadFolder(currentFolderId);
      },
      error: (err) => {
        this.selectedFolder = null;
      }
    })
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
    });
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
    });
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

  onHandleFileMore(eventData: any[]) {
    const [event, file] = eventData;
    event.stopPropagation();
    this.selectedFile = file;
  }

  onHandleFolderMore(eventData: any[]) {
    const [event, folder] = eventData;
    event.stopPropagation();
    this.selectedFolder = folder;
  }

  getCurrentFolderId() {
    return this.path.length > 0 ? this.path[this.path.length - 1].id : 0;
  }
}
