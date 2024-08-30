import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../book-storage.test-samples';

import { BookStorageFormService } from './book-storage-form.service';

describe('BookStorage Form Service', () => {
  let service: BookStorageFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BookStorageFormService);
  });

  describe('Service methods', () => {
    describe('createBookStorageFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBookStorageFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantity: expect.any(Object),
            library: expect.any(Object),
          }),
        );
      });

      it('passing IBookStorage should create a new form with FormGroup', () => {
        const formGroup = service.createBookStorageFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantity: expect.any(Object),
            library: expect.any(Object),
          }),
        );
      });
    });

    describe('getBookStorage', () => {
      it('should return NewBookStorage for default BookStorage initial value', () => {
        const formGroup = service.createBookStorageFormGroup(sampleWithNewData);

        const bookStorage = service.getBookStorage(formGroup) as any;

        expect(bookStorage).toMatchObject(sampleWithNewData);
      });

      it('should return NewBookStorage for empty BookStorage initial value', () => {
        const formGroup = service.createBookStorageFormGroup();

        const bookStorage = service.getBookStorage(formGroup) as any;

        expect(bookStorage).toMatchObject({});
      });

      it('should return IBookStorage', () => {
        const formGroup = service.createBookStorageFormGroup(sampleWithRequiredData);

        const bookStorage = service.getBookStorage(formGroup) as any;

        expect(bookStorage).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBookStorage should not enable id FormControl', () => {
        const formGroup = service.createBookStorageFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBookStorage should disable id FormControl', () => {
        const formGroup = service.createBookStorageFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
