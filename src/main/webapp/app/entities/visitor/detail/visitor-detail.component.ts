import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IVisitor } from '../visitor.model';

@Component({
  standalone: true,
  selector: 'jhi-visitor-detail',
  templateUrl: './visitor-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class VisitorDetailComponent {
  visitor = input<IVisitor | null>(null);

  previousState(): void {
    window.history.back();
  }
}
