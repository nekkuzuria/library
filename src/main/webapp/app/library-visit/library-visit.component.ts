import { Component, OnInit } from '@angular/core';
import { LibraryVisitService } from './librrary-visit.service';
import { IVisitVM } from './library-visit.model';
import { RouterModule } from '@angular/router';
import { PendingTaskService } from 'app/pending-task/pending-task.service';
import { PendingTaskStatus } from 'app/pending-task/pending-task-status.model';
import { IPendingTaskVM } from 'app/pending-task/pending-task.model';
import dayjs from 'dayjs/esm';

@Component({
  selector: 'jhi-library-visit',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './library-visit.component.html',
  styleUrl: './library-visit.component.scss',
})
export class LibraryVisitComponent implements OnInit {
  visits?: IPendingTaskVM[];
  PendingTaskStatus = PendingTaskStatus;

  constructor(private pendingTaskService: PendingTaskService) {}

  ngOnInit(): void {
    this.loadCurrentPendingTasks();
  }

  loadCurrentPendingTasks(): void {
    this.pendingTaskService.query().subscribe(response => {
      this.visits = response.body || [];
      this.visits = this.visits.filter(visit => visit.status === PendingTaskStatus.APPROVED);
    });
  }

  trackId(index: number, item: IPendingTaskVM): number {
    return item.id ?? 0;
  }

  convertDate(date: dayjs.Dayjs | null | undefined) {
    return date ? dayjs(date).format('DD/MM/YYYY') : null;
  }
}
