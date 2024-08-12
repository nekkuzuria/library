import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ILibrary, NewLibrary } from '../library.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILibrary for edit and NewLibraryFormGroupInput for create.
 */
type LibraryFormGroupInput = ILibrary | PartialWithRequiredKeyOf<NewLibrary>;

type LibraryFormDefaults = Pick<NewLibrary, 'id'>;

type LibraryFormGroupContent = {
  id: FormControl<ILibrary['id'] | NewLibrary['id']>;
  name: FormControl<ILibrary['name']>;
  establishedDate: FormControl<ILibrary['establishedDate']>;
};

export type LibraryFormGroup = FormGroup<LibraryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LibraryFormService {
  createLibraryFormGroup(library: LibraryFormGroupInput = { id: null }): LibraryFormGroup {
    const libraryRawValue = {
      ...this.getFormDefaults(),
      ...library,
    };
    return new FormGroup<LibraryFormGroupContent>({
      id: new FormControl(
        { value: libraryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(libraryRawValue.name),
      establishedDate: new FormControl(libraryRawValue.establishedDate),
    });
  }

  getLibrary(form: LibraryFormGroup): ILibrary | NewLibrary {
    return form.getRawValue() as ILibrary | NewLibrary;
  }

  resetForm(form: LibraryFormGroup, library: LibraryFormGroupInput): void {
    const libraryRawValue = { ...this.getFormDefaults(), ...library };
    form.reset(
      {
        ...libraryRawValue,
        id: { value: libraryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): LibraryFormDefaults {
    return {
      id: null,
    };
  }
}
