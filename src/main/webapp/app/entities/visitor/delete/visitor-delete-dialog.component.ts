import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IVisitor } from '../visitor.model';
import { VisitorService } from '../service/visitor.service';

@Component({
  standalone: true,
  templateUrl: './visitor-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class VisitorDeleteDialogComponent {
  visitor?: IVisitor;

  protected visitorService = inject(VisitorService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.visitorService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
