import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IBookVM, NewBook } from '../bookvm.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBook for edit and NewBookFormGroupInput for create.
 */
type BookFormGroupInput = IBookVM | PartialWithRequiredKeyOf<NewBook>;

type BookFormDefaults = Pick<NewBook, 'id'>;

type BookFormGroupContent = {
  id: FormControl<IBookVM['id'] | NewBook['id']>;
  title: FormControl<IBookVM['title']>;
  type: FormControl<IBookVM['type']>;
  genre: FormControl<IBookVM['genre']>;
  year: FormControl<IBookVM['year']>;
  totalPage: FormControl<IBookVM['totalPage']>;
  author: FormControl<IBookVM['author']>;
  cover: FormControl<IBookVM['cover']>;
  synopsis: FormControl<IBookVM['synopsis']>;
  bookStorageId: FormControl<IBookVM['bookStorageId']>;
  quantity: FormControl<IBookVM['quantity']>;
};

export type BookFormGroup = FormGroup<BookFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BookFormService {
  createBookFormGroup(book: BookFormGroupInput = { id: null }): BookFormGroup {
    const bookRawValue = {
      ...this.getFormDefaults(),
      ...book,
    };
    return new FormGroup<BookFormGroupContent>({
      id: new FormControl(
        { value: bookRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(bookRawValue.title, {
        validators: [Validators.required],
      }),
      type: new FormControl(bookRawValue.type),
      genre: new FormControl(bookRawValue.genre),
      year: new FormControl(bookRawValue.year),
      totalPage: new FormControl(bookRawValue.totalPage),
      author: new FormControl(bookRawValue.author),
      cover: new FormControl(bookRawValue.cover),
      synopsis: new FormControl(bookRawValue.synopsis),
      bookStorageId: new FormControl(bookRawValue.bookStorageId),
      quantity: new FormControl(bookRawValue.quantity),
    });
  }

  getBook(form: BookFormGroup): IBookVM | NewBook {
    return form.getRawValue() as IBookVM | NewBook;
  }

  resetForm(form: BookFormGroup, book: BookFormGroupInput): void {
    const bookRawValue = { ...this.getFormDefaults(), ...book };
    form.reset(
      {
        ...bookRawValue,
        id: { value: bookRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): BookFormDefaults {
    return {
      id: null,
    };
  }
}
