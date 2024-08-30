import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../visitor-book-storage.test-samples';

import { VisitorBookStorageFormService } from './visitor-book-storage-form.service';

describe('VisitorBookStorage Form Service', () => {
  let service: VisitorBookStorageFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VisitorBookStorageFormService);
  });

  describe('Service methods', () => {
    describe('createVisitorBookStorageFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createVisitorBookStorageFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            borrowDate: expect.any(Object),
            returnDate: expect.any(Object),
            visitor: expect.any(Object),
            book: expect.any(Object),
          }),
        );
      });

      it('passing IVisitorBookStorage should create a new form with FormGroup', () => {
        const formGroup = service.createVisitorBookStorageFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            borrowDate: expect.any(Object),
            returnDate: expect.any(Object),
            visitor: expect.any(Object),
            book: expect.any(Object),
          }),
        );
      });
    });

    describe('getVisitorBookStorage', () => {
      it('should return NewVisitorBookStorage for default VisitorBookStorage initial value', () => {
        const formGroup = service.createVisitorBookStorageFormGroup(sampleWithNewData);

        const visitorBookStorage = service.getVisitorBookStorage(formGroup) as any;

        expect(visitorBookStorage).toMatchObject(sampleWithNewData);
      });

      it('should return NewVisitorBookStorage for empty VisitorBookStorage initial value', () => {
        const formGroup = service.createVisitorBookStorageFormGroup();

        const visitorBookStorage = service.getVisitorBookStorage(formGroup) as any;

        expect(visitorBookStorage).toMatchObject({});
      });

      it('should return IVisitorBookStorage', () => {
        const formGroup = service.createVisitorBookStorageFormGroup(sampleWithRequiredData);

        const visitorBookStorage = service.getVisitorBookStorage(formGroup) as any;

        expect(visitorBookStorage).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IVisitorBookStorage should not enable id FormControl', () => {
        const formGroup = service.createVisitorBookStorageFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewVisitorBookStorage should disable id FormControl', () => {
        const formGroup = service.createVisitorBookStorageFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
