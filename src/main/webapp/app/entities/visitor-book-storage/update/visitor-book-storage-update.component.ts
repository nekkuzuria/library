import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IVisitor } from 'app/entities/visitor/visitor.model';
import { VisitorService } from 'app/entities/visitor/service/visitor.service';
import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { VisitorBookStorageService } from '../service/visitor-book-storage.service';
import { IVisitorBookStorage } from '../visitor-book-storage.model';
import { VisitorBookStorageFormService, VisitorBookStorageFormGroup } from './visitor-book-storage-form.service';

@Component({
  standalone: true,
  selector: 'jhi-visitor-book-storage-update',
  templateUrl: './visitor-book-storage-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class VisitorBookStorageUpdateComponent implements OnInit {
  isSaving = false;
  visitorBookStorage: IVisitorBookStorage | null = null;

  visitorsSharedCollection: IVisitor[] = [];
  booksSharedCollection: IBook[] = [];

  protected visitorBookStorageService = inject(VisitorBookStorageService);
  protected visitorBookStorageFormService = inject(VisitorBookStorageFormService);
  protected visitorService = inject(VisitorService);
  protected bookService = inject(BookService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: VisitorBookStorageFormGroup = this.visitorBookStorageFormService.createVisitorBookStorageFormGroup();

  compareVisitor = (o1: IVisitor | null, o2: IVisitor | null): boolean => this.visitorService.compareVisitor(o1, o2);

  compareBook = (o1: IBook | null, o2: IBook | null): boolean => this.bookService.compareBook(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ visitorBookStorage }) => {
      this.visitorBookStorage = visitorBookStorage;
      if (visitorBookStorage) {
        this.updateForm(visitorBookStorage);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const visitorBookStorage = this.visitorBookStorageFormService.getVisitorBookStorage(this.editForm);
    if (visitorBookStorage.id !== null) {
      this.subscribeToSaveResponse(this.visitorBookStorageService.update(visitorBookStorage));
    } else {
      this.subscribeToSaveResponse(this.visitorBookStorageService.create(visitorBookStorage));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVisitorBookStorage>>): void {
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

  protected updateForm(visitorBookStorage: IVisitorBookStorage): void {
    this.visitorBookStorage = visitorBookStorage;
    this.visitorBookStorageFormService.resetForm(this.editForm, visitorBookStorage);

    this.visitorsSharedCollection = this.visitorService.addVisitorToCollectionIfMissing<IVisitor>(
      this.visitorsSharedCollection,
      visitorBookStorage.visitor,
    );
    this.booksSharedCollection = this.bookService.addBookToCollectionIfMissing<IBook>(this.booksSharedCollection, visitorBookStorage.book);
  }

  protected loadRelationshipsOptions(): void {
    this.visitorService
      .query()
      .pipe(map((res: HttpResponse<IVisitor[]>) => res.body ?? []))
      .pipe(
        map((visitors: IVisitor[]) =>
          this.visitorService.addVisitorToCollectionIfMissing<IVisitor>(visitors, this.visitorBookStorage?.visitor),
        ),
      )
      .subscribe((visitors: IVisitor[]) => (this.visitorsSharedCollection = visitors));

    this.bookService
      .query()
      .pipe(map((res: HttpResponse<IBook[]>) => res.body ?? []))
      .pipe(map((books: IBook[]) => this.bookService.addBookToCollectionIfMissing<IBook>(books, this.visitorBookStorage?.book)))
      .subscribe((books: IBook[]) => (this.booksSharedCollection = books));
  }
}
