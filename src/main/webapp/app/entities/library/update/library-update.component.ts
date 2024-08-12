import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ILibrary } from '../library.model';
import { LibraryService } from '../service/library.service';
import { LibraryFormService, LibraryFormGroup } from './library-form.service';

@Component({
  standalone: true,
  selector: 'jhi-library-update',
  templateUrl: './library-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class LibraryUpdateComponent implements OnInit {
  isSaving = false;
  library: ILibrary | null = null;

  protected libraryService = inject(LibraryService);
  protected libraryFormService = inject(LibraryFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LibraryFormGroup = this.libraryFormService.createLibraryFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ library }) => {
      this.library = library;
      if (library) {
        this.updateForm(library);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const library = this.libraryFormService.getLibrary(this.editForm);
    if (library.id !== null) {
      this.subscribeToSaveResponse(this.libraryService.update(library));
    } else {
      this.subscribeToSaveResponse(this.libraryService.create(library));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILibrary>>): void {
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

  protected updateForm(library: ILibrary): void {
    this.library = library;
    this.libraryFormService.resetForm(this.editForm, library);
  }
}
