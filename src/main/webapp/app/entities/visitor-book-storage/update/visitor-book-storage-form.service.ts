import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IVisitorBookStorage, NewVisitorBookStorage } from '../visitor-book-storage.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IVisitorBookStorage for edit and NewVisitorBookStorageFormGroupInput for create.
 */
type VisitorBookStorageFormGroupInput = IVisitorBookStorage | PartialWithRequiredKeyOf<NewVisitorBookStorage>;

type VisitorBookStorageFormDefaults = Pick<NewVisitorBookStorage, 'id'>;

type VisitorBookStorageFormGroupContent = {
  id: FormControl<IVisitorBookStorage['id'] | NewVisitorBookStorage['id']>;
  borrowDate: FormControl<IVisitorBookStorage['borrowDate']>;
  returnDate: FormControl<IVisitorBookStorage['returnDate']>;
  visitor: FormControl<IVisitorBookStorage['visitor']>;
  book: FormControl<IVisitorBookStorage['book']>;
};

export type VisitorBookStorageFormGroup = FormGroup<VisitorBookStorageFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class VisitorBookStorageFormService {
  createVisitorBookStorageFormGroup(visitorBookStorage: VisitorBookStorageFormGroupInput = { id: null }): VisitorBookStorageFormGroup {
    const visitorBookStorageRawValue = {
      ...this.getFormDefaults(),
      ...visitorBookStorage,
    };
    return new FormGroup<VisitorBookStorageFormGroupContent>({
      id: new FormControl(
        { value: visitorBookStorageRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      borrowDate: new FormControl(visitorBookStorageRawValue.borrowDate),
      returnDate: new FormControl(visitorBookStorageRawValue.returnDate),
      visitor: new FormControl(visitorBookStorageRawValue.visitor),
      book: new FormControl(visitorBookStorageRawValue.book),
    });
  }

  getVisitorBookStorage(form: VisitorBookStorageFormGroup): IVisitorBookStorage | NewVisitorBookStorage {
    return form.getRawValue() as IVisitorBookStorage | NewVisitorBookStorage;
  }

  resetForm(form: VisitorBookStorageFormGroup, visitorBookStorage: VisitorBookStorageFormGroupInput): void {
    const visitorBookStorageRawValue = { ...this.getFormDefaults(), ...visitorBookStorage };
    form.reset(
      {
        ...visitorBookStorageRawValue,
        id: { value: visitorBookStorageRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): VisitorBookStorageFormDefaults {
    return {
      id: null,
    };
  }
}
