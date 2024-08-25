import { Component, OnInit } from '@angular/core';
import { IPersonalStorage } from './personal-storage.model';
import { PersonalStorageService } from './personal-storage.service';
import { RouterModule } from '@angular/router';
import { NewPendingTaskReturn } from 'app/pending-task/pending-task.model';
import { PendingTaskService } from 'app/pending-task/pending-task.service';
import dayjs from 'dayjs/esm';
import SharedModule from 'app/shared/shared.module';
import { AlertService } from 'app/core/util/alert.service';

@Component({
  selector: 'jhi-personal-storage',
  templateUrl: './personal-storage.component.html',
  standalone: true,
  imports: [RouterModule, SharedModule],
})
export class PersonalStorageComponent implements OnInit {
  visitorBookStorages?: IPersonalStorage[];
  errorMessage: string | null = null;

  constructor(
    private personalStorageService: PersonalStorageService,
    private pendingTaskService: PendingTaskService,
    private alertService: AlertService,
  ) {}

  ngOnInit(): void {
    this.loadCurrentUserBookStorage();
  }

  loadCurrentUserBookStorage(): void {
    this.personalStorageService.query().subscribe(response => {
      this.visitorBookStorages = response.body || [];
      this.visitorBookStorages = this.visitorBookStorages.filter(vbs => vbs.returnDate === null);
    });
  }

  convertDate(date: dayjs.Dayjs | null | undefined) {
    return date ? dayjs(date).format('DD/MM/YYYY') : null;
  }

  getExpectedDate(date: dayjs.Dayjs | null | undefined) {
    return date ? dayjs(date).add(7, 'day').format('DD/MM/YYYY') : null;
  }

  trackId(index: number, item: IPersonalStorage): number {
    return item.id;
  }

  returnSelectedBook(vbs: IPersonalStorage): void {
    if (vbs.id !== null) {
      const pendingTask: NewPendingTaskReturn = {
        id: null,
        bookId: vbs.bookId,
        type: 'RETURN',
        quantity: vbs.quantity,
        visitorBookStorageId: vbs.id,
      };
      this.pendingTaskService.create(pendingTask).subscribe({
        next: response => {
          alert('Return request created successfully');
          this.loadCurrentUserBookStorage();
        },
        error: error => {
          if (error.status === 409) {
            this.alertService.addAlert({ type: 'danger', message: error.error, timeout: 5000 });
          } else {
            this.alertService.addAlert({ type: 'danger', message: 'An unexpected error occurred.', timeout: 5000 });
          }
        },
      });
    }
  }

  clearErrorMessage(): void {
    this.errorMessage = null;
  }
}
