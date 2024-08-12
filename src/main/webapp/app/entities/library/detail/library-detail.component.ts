import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { ILibrary } from '../library.model';

@Component({
  standalone: true,
  selector: 'jhi-library-detail',
  templateUrl: './library-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class LibraryDetailComponent {
  library = input<ILibrary | null>(null);

  previousState(): void {
    window.history.back();
  }
}
