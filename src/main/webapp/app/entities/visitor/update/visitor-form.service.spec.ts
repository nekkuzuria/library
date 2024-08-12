import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../visitor.test-samples';

import { VisitorFormService } from './visitor-form.service';

describe('Visitor Form Service', () => {
  let service: VisitorFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VisitorFormService);
  });

  describe('Service methods', () => {
    describe('createVisitorFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createVisitorFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            email: expect.any(Object),
            phoneNumber: expect.any(Object),
            dateOfBirth: expect.any(Object),
            membershipStatus: expect.any(Object),
          }),
        );
      });

      it('passing IVisitor should create a new form with FormGroup', () => {
        const formGroup = service.createVisitorFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            email: expect.any(Object),
            phoneNumber: expect.any(Object),
            dateOfBirth: expect.any(Object),
            membershipStatus: expect.any(Object),
          }),
        );
      });
    });

    describe('getVisitor', () => {
      it('should return NewVisitor for default Visitor initial value', () => {
        const formGroup = service.createVisitorFormGroup(sampleWithNewData);

        const visitor = service.getVisitor(formGroup) as any;

        expect(visitor).toMatchObject(sampleWithNewData);
      });

      it('should return NewVisitor for empty Visitor initial value', () => {
        const formGroup = service.createVisitorFormGroup();

        const visitor = service.getVisitor(formGroup) as any;

        expect(visitor).toMatchObject({});
      });

      it('should return IVisitor', () => {
        const formGroup = service.createVisitorFormGroup(sampleWithRequiredData);

        const visitor = service.getVisitor(formGroup) as any;

        expect(visitor).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IVisitor should not enable id FormControl', () => {
        const formGroup = service.createVisitorFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewVisitor should disable id FormControl', () => {
        const formGroup = service.createVisitorFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
