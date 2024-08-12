import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IBookStorage } from '../book-storage.model';
import { BookStorageService } from '../service/book-storage.service';

@Component({
  standalone: true,
  templateUrl: './book-storage-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class BookStorageDeleteDialogComponent {
  bookStorage?: IBookStorage;

  protected bookStorageService = inject(BookStorageService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.bookStorageService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
