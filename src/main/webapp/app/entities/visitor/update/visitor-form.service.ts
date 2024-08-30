import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IVisitor, NewVisitor } from '../visitor.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IVisitor for edit and NewVisitorFormGroupInput for create.
 */
type VisitorFormGroupInput = IVisitor | PartialWithRequiredKeyOf<NewVisitor>;

type VisitorFormDefaults = Pick<NewVisitor, 'id' | 'membershipStatus'>;

type VisitorFormGroupContent = {
  id: FormControl<IVisitor['id'] | NewVisitor['id']>;
  name: FormControl<IVisitor['name']>;
  email: FormControl<IVisitor['email']>;
  phoneNumber: FormControl<IVisitor['phoneNumber']>;
  dateOfBirth: FormControl<IVisitor['dateOfBirth']>;
  membershipStatus: FormControl<IVisitor['membershipStatus']>;
  address: FormControl<IVisitor['address']>;
  library: FormControl<IVisitor['library']>;
  user: FormControl<IVisitor['user']>;
};

export type VisitorFormGroup = FormGroup<VisitorFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class VisitorFormService {
  createVisitorFormGroup(visitor: VisitorFormGroupInput = { id: null }): VisitorFormGroup {
    const visitorRawValue = {
      ...this.getFormDefaults(),
      ...visitor,
    };
    return new FormGroup<VisitorFormGroupContent>({
      id: new FormControl(
        { value: visitorRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(visitorRawValue.name),
      email: new FormControl(visitorRawValue.email),
      phoneNumber: new FormControl(visitorRawValue.phoneNumber),
      dateOfBirth: new FormControl(visitorRawValue.dateOfBirth),
      membershipStatus: new FormControl(visitorRawValue.membershipStatus),
      address: new FormControl(visitorRawValue.address),
      library: new FormControl(visitorRawValue.library),
      user: new FormControl(visitorRawValue.user),
    });
  }

  getVisitor(form: VisitorFormGroup): IVisitor | NewVisitor {
    return form.getRawValue() as IVisitor | NewVisitor;
  }

  resetForm(form: VisitorFormGroup, visitor: VisitorFormGroupInput): void {
    const visitorRawValue = { ...this.getFormDefaults(), ...visitor };
    form.reset(
      {
        ...visitorRawValue,
        id: { value: visitorRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): VisitorFormDefaults {
    return {
      id: null,
      membershipStatus: false,
    };
  }
}
