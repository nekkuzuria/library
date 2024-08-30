import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ILibrary } from '../library.model';
import { LibraryService } from '../service/library.service';

@Component({
  standalone: true,
  templateUrl: './library-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class LibraryDeleteDialogComponent {
  library?: ILibrary;

  protected libraryService = inject(LibraryService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.libraryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
