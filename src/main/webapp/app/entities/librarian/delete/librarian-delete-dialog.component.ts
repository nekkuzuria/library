import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ILibrarian } from '../librarian.model';
import { LibrarianService } from '../service/librarian.service';

@Component({
  standalone: true,
  templateUrl: './librarian-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class LibrarianDeleteDialogComponent {
  librarian?: ILibrarian;

  protected librarianService = inject(LibrarianService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.librarianService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
