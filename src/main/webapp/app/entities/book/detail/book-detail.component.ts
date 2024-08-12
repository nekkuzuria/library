import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IBook } from '../book.model';

@Component({
  standalone: true,
  selector: 'jhi-book-detail',
  templateUrl: './book-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class BookDetailComponent {
  book = input<IBook | null>(null);

  previousState(): void {
    window.history.back();
  }
}
