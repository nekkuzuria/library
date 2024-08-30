import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IVisitorBookStorage } from '../visitor-book-storage.model';

@Component({
  standalone: true,
  selector: 'jhi-visitor-book-storage-detail',
  templateUrl: './visitor-book-storage-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class VisitorBookStorageDetailComponent {
  visitorBookStorage = input<IVisitorBookStorage | null>(null);

  previousState(): void {
    window.history.back();
  }
}
