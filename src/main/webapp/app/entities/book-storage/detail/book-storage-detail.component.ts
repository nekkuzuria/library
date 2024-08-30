import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IBookStorage } from '../book-storage.model';

@Component({
  standalone: true,
  selector: 'jhi-book-storage-detail',
  templateUrl: './book-storage-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class BookStorageDetailComponent {
  bookStorage = input<IBookStorage | null>(null);

  previousState(): void {
    window.history.back();
  }
}
