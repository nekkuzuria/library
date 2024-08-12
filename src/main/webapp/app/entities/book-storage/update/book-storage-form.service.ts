import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IBookStorage, NewBookStorage } from '../book-storage.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBookStorage for edit and NewBookStorageFormGroupInput for create.
 */
type BookStorageFormGroupInput = IBookStorage | PartialWithRequiredKeyOf<NewBookStorage>;

type BookStorageFormDefaults = Pick<NewBookStorage, 'id'>;

type BookStorageFormGroupContent = {
  id: FormControl<IBookStorage['id'] | NewBookStorage['id']>;
  quantity: FormControl<IBookStorage['quantity']>;
};

export type BookStorageFormGroup = FormGroup<BookStorageFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BookStorageFormService {
  createBookStorageFormGroup(bookStorage: BookStorageFormGroupInput = { id: null }): BookStorageFormGroup {
    const bookStorageRawValue = {
      ...this.getFormDefaults(),
      ...bookStorage,
    };
    return new FormGroup<BookStorageFormGroupContent>({
      id: new FormControl(
        { value: bookStorageRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      quantity: new FormControl(bookStorageRawValue.quantity),
    });
  }

  getBookStorage(form: BookStorageFormGroup): IBookStorage | NewBookStorage {
    return form.getRawValue() as IBookStorage | NewBookStorage;
  }

  resetForm(form: BookStorageFormGroup, bookStorage: BookStorageFormGroupInput): void {
    const bookStorageRawValue = { ...this.getFormDefaults(), ...bookStorage };
    form.reset(
      {
        ...bookStorageRawValue,
        id: { value: bookStorageRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): BookStorageFormDefaults {
    return {
      id: null,
    };
  }
}
