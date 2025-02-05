import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IBookStorage } from 'app/entities/book-storage/book-storage.model';
import { BookStorageService } from 'app/entities/book-storage/service/book-storage.service';
import { IFile } from 'app/entities/file/file.model';
import { FileService } from 'app/entities/file/service/file.service';
import { BookType } from 'app/entities/enumerations/book-type.model';
import { Genre } from 'app/entities/enumerations/genre.model';
import { BookService } from '../service/book.service';
import { IBook } from '../book.model';
import { BookFormService, BookFormGroup } from './book-form.service';

@Component({
  standalone: true,
  selector: 'jhi-book-update',
  templateUrl: './book-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class BookUpdateComponent implements OnInit {
  isSaving = false;
  book: IBook | null = null;
  bookTypeValues = Object.keys(BookType);
  genreValues = Object.keys(Genre);
  file = null;

  bookStoragesSharedCollection: IBookStorage[] = [];
  filesCollection: IFile[] = [];
  imagePreview: string | ArrayBuffer | null = null;

  protected bookService = inject(BookService);
  protected bookFormService = inject(BookFormService);
  protected bookStorageService = inject(BookStorageService);
  protected fileService = inject(FileService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: BookFormGroup = this.bookFormService.createBookFormGroup();

  compareBookStorage = (o1: IBookStorage | null, o2: IBookStorage | null): boolean => this.bookStorageService.compareBookStorage(o1, o2);

  compareFile = (o1: IFile | null, o2: IFile | null): boolean => this.fileService.compareFile(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ book }) => {
      this.book = book;
      if (book) {
        this.updateForm(book);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const book = this.bookFormService.getBook(this.editForm);
    if (book.id !== null) {
      if (this.file) {
        console.log(this.file);
        this.subscribeToSaveResponse(this.bookService.update(book, this.file));
      } else {
        this.subscribeToSaveResponse(this.bookService.update(book));
      }
    } else {
      if (this.file) {
        this.subscribeToSaveResponse(this.bookService.create(book, this.file));
      } else {
        this.subscribeToSaveResponse(this.bookService.create(book));
      }
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBook>>): void {
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

  protected updateForm(book: IBook): void {
    this.book = book;
    this.bookFormService.resetForm(this.editForm, book);

    this.bookStoragesSharedCollection = this.bookStorageService.addBookStorageToCollectionIfMissing<IBookStorage>(
      this.bookStoragesSharedCollection,
      book.bookStorageId,
    );
    this.filesCollection = this.fileService.addFileToCollectionIfMissing<IFile>(this.filesCollection, book.file);

    if (book.file) {
      this.fileService.find(book.file.id).subscribe((response: HttpResponse<IFile>) => {
        const file = response.body;
        if (file && file.image) {
          this.imagePreview = 'data:image/jpeg;base64,' + file.image;
        }
      });
    }
  }

  protected loadRelationshipsOptions(): void {
    this.bookStorageService
      .query()
      .pipe(map((res: HttpResponse<IBookStorage[]>) => res.body ?? []))
      .pipe(
        map((bookStorages: IBookStorage[]) =>
          this.bookStorageService.addBookStorageToCollectionIfMissing<IBookStorage>(bookStorages, this.book?.bookStorageId),
        ),
      )
      .subscribe((bookStorages: IBookStorage[]) => (this.bookStoragesSharedCollection = bookStorages));
  }

  onImageChange(event: any): void {
    this.file = event.target.files[0];
    if (this.file) {
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result;
      };
      reader.readAsDataURL(this.file);
    }
  }

  restrictNonNumeric(event: KeyboardEvent): void {
    if (event.key === 'e' || event.key === 'E' || event.key === '-' || event.key === '+' || event.key === '.') {
      event.preventDefault();
    }
  }
}
