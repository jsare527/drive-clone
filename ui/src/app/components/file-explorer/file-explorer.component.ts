import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FileDTO, FolderDTO, FolderResponse } from '../../models/folder';
import { MatIcon, MatIconModule } from "@angular/material/icon";
import { MatMenuModule } from "@angular/material/menu";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-file-explorer',
  standalone: true,
  imports: [MatIconModule, MatMenuModule, MatProgressBarModule, MatIcon, MatButtonModule],
  templateUrl: './file-explorer.component.html',
  styleUrl: './file-explorer.component.scss'
})
export class FileExplorerComponent {
  @Input() response: FolderResponse | null = null;
  @Input() viewType: 'home' | 'trash' = 'home';
  @Input() downloadProgress: number = 0;
  @Input() isLoading: boolean = false;
  @Input() path: any[] = [];

  @Output() fileDownload = new EventEmitter<any>();
  @Output() fileDelete = new EventEmitter<any>();
  @Output() fileSelected = new EventEmitter<any>();
  @Output() folderDownload = new EventEmitter<any>();
  @Output() folderDelete = new EventEmitter<any>();
  @Output() folderSelected = new EventEmitter<any>();
  @Output() openFolder = new EventEmitter<any>();
  @Output() fileMore = new EventEmitter<any>();
  @Output() folderMore = new EventEmitter<any>();

  imageTypes = ['image/png', 'image/jpeg', 'image/gif'];
}
