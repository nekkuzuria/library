import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ILibrarian } from '../librarian.model';
import { LibrarianService } from '../service/librarian.service';
import { LibrarianFormService, LibrarianFormGroup } from './librarian-form.service';

@Component({
  standalone: true,
  selector: 'jhi-librarian-update',
  templateUrl: './librarian-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class LibrarianUpdateComponent implements OnInit {
  isSaving = false;
  librarian: ILibrarian | null = null;

  protected librarianService = inject(LibrarianService);
  protected librarianFormService = inject(LibrarianFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LibrarianFormGroup = this.librarianFormService.createLibrarianFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ librarian }) => {
      this.librarian = librarian;
      if (librarian) {
        this.updateForm(librarian);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const librarian = this.librarianFormService.getLibrarian(this.editForm);
    if (librarian.id !== null) {
      this.subscribeToSaveResponse(this.librarianService.update(librarian));
    } else {
      this.subscribeToSaveResponse(this.librarianService.create(librarian));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILibrarian>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(librarian: ILibrarian): void {
    this.librarian = librarian;
    this.librarianFormService.resetForm(this.editForm, librarian);
  }
}
