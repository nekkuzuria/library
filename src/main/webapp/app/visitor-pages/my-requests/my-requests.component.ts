import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { PendingTaskStatus } from 'app/pending-task/pending-task-status.model';
import { IPendingTaskVM } from 'app/pending-task/pending-task.model';
import { PendingTaskService } from 'app/pending-task/pending-task.service';
import SharedModule from 'app/shared/shared.module';
import dayjs from 'dayjs';

@Component({
  selector: 'jhi-my-requests',
  standalone: true,
  imports: [RouterModule, SharedModule],
  templateUrl: './my-requests.component.html',
  styleUrl: './my-requests.component.scss',
})
export class MyRequestsComponent implements OnInit {
  PendingTaskStatus = PendingTaskStatus;
  pendingTasks?: IPendingTaskVM[];
  selectedPendingTask: IPendingTaskVM = {} as IPendingTaskVM;

  constructor(private pendingTaskService: PendingTaskService) {}

  ngOnInit(): void {
    this.loadCurrentPendingTasks();
  }

  loadCurrentPendingTasks(): void {
    this.pendingTaskService.queryVisitor().subscribe(response => {
      this.pendingTasks = response.body || [];
    });
  }

  trackId(index: number, item: IPendingTaskVM): number {
    return item.id ?? 0;
  }

  updateSelected(pendingTask: IPendingTaskVM): void {
    this.selectedPendingTask = pendingTask;
  }

  convertDate(date: dayjs.Dayjs | null | undefined) {
    return date ? dayjs(date).format('DD/MM/YYYY') : null;
  }
}
