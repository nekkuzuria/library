import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IVisit, NewVisit } from '../visit.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IVisit for edit and NewVisitFormGroupInput for create.
 */
type VisitFormGroupInput = IVisit | PartialWithRequiredKeyOf<NewVisit>;

type VisitFormDefaults = Pick<NewVisit, 'id'>;

type VisitFormGroupContent = {
  id: FormControl<IVisit['id'] | NewVisit['id']>;
  date: FormControl<IVisit['date']>;
  library: FormControl<IVisit['library']>;
  librarian: FormControl<IVisit['librarian']>;
  visitor: FormControl<IVisit['visitor']>;
};

export type VisitFormGroup = FormGroup<VisitFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class VisitFormService {
  createVisitFormGroup(visit: VisitFormGroupInput = { id: null }): VisitFormGroup {
    const visitRawValue = {
      ...this.getFormDefaults(),
      ...visit,
    };
    return new FormGroup<VisitFormGroupContent>({
      id: new FormControl(
        { value: visitRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      date: new FormControl(visitRawValue.date),
      library: new FormControl(visitRawValue.library),
      librarian: new FormControl(visitRawValue.librarian),
      visitor: new FormControl(visitRawValue.visitor),
    });
  }

  getVisit(form: VisitFormGroup): IVisit | NewVisit {
    return form.getRawValue() as IVisit | NewVisit;
  }

  resetForm(form: VisitFormGroup, visit: VisitFormGroupInput): void {
    const visitRawValue = { ...this.getFormDefaults(), ...visit };
    form.reset(
      {
        ...visitRawValue,
        id: { value: visitRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): VisitFormDefaults {
    return {
      id: null,
    };
  }
}
