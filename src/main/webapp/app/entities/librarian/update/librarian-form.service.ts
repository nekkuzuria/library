import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ILibrarian, NewLibrarian } from '../librarian.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILibrarian for edit and NewLibrarianFormGroupInput for create.
 */
type LibrarianFormGroupInput = ILibrarian | PartialWithRequiredKeyOf<NewLibrarian>;

type LibrarianFormDefaults = Pick<NewLibrarian, 'id'>;

type LibrarianFormGroupContent = {
  id: FormControl<ILibrarian['id'] | NewLibrarian['id']>;
  name: FormControl<ILibrarian['name']>;
  email: FormControl<ILibrarian['email']>;
  phoneNumber: FormControl<ILibrarian['phoneNumber']>;
  dateOfBirth: FormControl<ILibrarian['dateOfBirth']>;
};

export type LibrarianFormGroup = FormGroup<LibrarianFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LibrarianFormService {
  createLibrarianFormGroup(librarian: LibrarianFormGroupInput = { id: null }): LibrarianFormGroup {
    const librarianRawValue = {
      ...this.getFormDefaults(),
      ...librarian,
    };
    return new FormGroup<LibrarianFormGroupContent>({
      id: new FormControl(
        { value: librarianRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(librarianRawValue.name),
      email: new FormControl(librarianRawValue.email),
      phoneNumber: new FormControl(librarianRawValue.phoneNumber),
      dateOfBirth: new FormControl(librarianRawValue.dateOfBirth),
    });
  }

  getLibrarian(form: LibrarianFormGroup): ILibrarian | NewLibrarian {
    return form.getRawValue() as ILibrarian | NewLibrarian;
  }

  resetForm(form: LibrarianFormGroup, librarian: LibrarianFormGroupInput): void {
    const librarianRawValue = { ...this.getFormDefaults(), ...librarian };
    form.reset(
      {
        ...librarianRawValue,
        id: { value: librarianRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): LibrarianFormDefaults {
    return {
      id: null,
    };
  }
}
