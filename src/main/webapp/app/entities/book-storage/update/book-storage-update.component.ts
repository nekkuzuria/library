import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IBookStorage } from '../book-storage.model';
import { BookStorageService } from '../service/book-storage.service';
import { BookStorageFormService, BookStorageFormGroup } from './book-storage-form.service';

@Component({
  standalone: true,
  selector: 'jhi-book-storage-update',
  templateUrl: './book-storage-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class BookStorageUpdateComponent implements OnInit {
  isSaving = false;
  bookStorage: IBookStorage | null = null;

  protected bookStorageService = inject(BookStorageService);
  protected bookStorageFormService = inject(BookStorageFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: BookStorageFormGroup = this.bookStorageFormService.createBookStorageFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bookStorage }) => {
      this.bookStorage = bookStorage;
      if (bookStorage) {
        this.updateForm(bookStorage);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const bookStorage = this.bookStorageFormService.getBookStorage(this.editForm);
    if (bookStorage.id !== null) {
      this.subscribeToSaveResponse(this.bookStorageService.update(bookStorage));
    } else {
      this.subscribeToSaveResponse(this.bookStorageService.create(bookStorage));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBookStorage>>): void {
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

  protected updateForm(bookStorage: IBookStorage): void {
    this.bookStorage = bookStorage;
    this.bookStorageFormService.resetForm(this.editForm, bookStorage);
  }
}
