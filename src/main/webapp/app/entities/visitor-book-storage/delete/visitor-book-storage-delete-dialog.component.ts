import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IVisitorBookStorage } from '../visitor-book-storage.model';
import { VisitorBookStorageService } from '../service/visitor-book-storage.service';

@Component({
  standalone: true,
  templateUrl: './visitor-book-storage-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class VisitorBookStorageDeleteDialogComponent {
  visitorBookStorage?: IVisitorBookStorage;

  protected visitorBookStorageService = inject(VisitorBookStorageService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.visitorBookStorageService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
