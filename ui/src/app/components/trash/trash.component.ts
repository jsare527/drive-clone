import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { TrashService } from '../../services/trash.service';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { TrashDTO } from '../../models/trashDTO';
import { PageEvent, MatPaginatorModule } from '@angular/material/paginator';
import { MatIconModule } from "@angular/material/icon";
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-trash',
  standalone: true,
  imports: [MatIconModule, MatPaginatorModule, CommonModule, MatButtonModule, MatTableModule],
  templateUrl: './trash.component.html',
  styleUrl: './trash.component.scss'
})
export class TrashComponent implements OnInit {
  dataSource = new MatTableDataSource<TrashDTO>([]);
  displayedColumns: string[] = ['name', 'originalPath', 'deletedAt', 'actions'];
  totalElements = 0;
  pageSize = 20;
  destroyRef = inject(DestroyRef);

  constructor(private trashService: TrashService) {}
  
  ngOnInit(): void {
    this.loadTrash(0, this.pageSize);
  }

  loadTrash(pageIndex: number, pageSize: number) {
    this.trashService.getTrash(pageIndex, pageSize)
    .pipe(takeUntilDestroyed(this.destroyRef))
    .subscribe(response => {
      this.dataSource.data = response.content;
      this.totalElements = response.totalElements;
    });
  }

  onPageChange(event: PageEvent) {
    this.loadTrash(event.pageIndex, event.pageSize);
  }
}
