import { Component, OnInit } from '@angular/core';
import { IPendingTaskVM } from './pending-task.model';
import { RouterModule } from '@angular/router';
import { PendingTaskService } from './pending-task.service';
import { FormsModule } from '@angular/forms';
import SharedModule from 'app/shared/shared.module';
import { sortStateSignal, SortDirective, SortByDirective, type SortState, SortService } from 'app/shared/sort';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { ItemCountComponent } from 'app/shared/pagination';
import dayjs from 'dayjs/esm';

@Component({
  selector: 'jhi-pending-task',
  standalone: true,
  imports: [
    RouterModule,
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
    ItemCountComponent,
  ],
  templateUrl: './pending-task.component.html',
  styleUrl: './pending-task.component.scss',
})
export class PendingTaskComponent implements OnInit {
  pendingTasks?: IPendingTaskVM[];

  constructor(private pendingTaskService: PendingTaskService) {}

  ngOnInit(): void {
    this.loadCurrentPendingTasks();
  }
  loadCurrentPendingTasks(): void {
    this.pendingTaskService.query().subscribe(response => {
      this.pendingTasks = (response.body || []).map(task => {
        const convertedDate = task.createdDate ? dayjs(task.createdDate) : null;
        return {
          ...task,
          createdDate: convertedDate,
        };
      });
    });
  }
  trackId(index: number, item: IPendingTaskVM): number {
    return item.id ?? 0;
  }

  updateTaskStatus(taskId: number, status: string): void {
    this.pendingTaskService.updateStatus(taskId, status).subscribe({
      next: () => {
        console.log('Pending task updated successfully');
        this.loadCurrentPendingTasks();
      },
      error: err => console.error('Error updating pending task status', err),
    });
  }
}
