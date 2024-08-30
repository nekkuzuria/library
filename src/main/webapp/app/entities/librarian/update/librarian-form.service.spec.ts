import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../librarian.test-samples';

import { LibrarianFormService } from './librarian-form.service';

describe('Librarian Form Service', () => {
  let service: LibrarianFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LibrarianFormService);
  });

  describe('Service methods', () => {
    describe('createLibrarianFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLibrarianFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            email: expect.any(Object),
            phoneNumber: expect.any(Object),
            dateOfBirth: expect.any(Object),
            library: expect.any(Object),
            location: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });

      it('passing ILibrarian should create a new form with FormGroup', () => {
        const formGroup = service.createLibrarianFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            email: expect.any(Object),
            phoneNumber: expect.any(Object),
            dateOfBirth: expect.any(Object),
            library: expect.any(Object),
            location: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });
    });

    describe('getLibrarian', () => {
      it('should return NewLibrarian for default Librarian initial value', () => {
        const formGroup = service.createLibrarianFormGroup(sampleWithNewData);

        const librarian = service.getLibrarian(formGroup) as any;

        expect(librarian).toMatchObject(sampleWithNewData);
      });

      it('should return NewLibrarian for empty Librarian initial value', () => {
        const formGroup = service.createLibrarianFormGroup();

        const librarian = service.getLibrarian(formGroup) as any;

        expect(librarian).toMatchObject({});
      });

      it('should return ILibrarian', () => {
        const formGroup = service.createLibrarianFormGroup(sampleWithRequiredData);

        const librarian = service.getLibrarian(formGroup) as any;

        expect(librarian).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILibrarian should not enable id FormControl', () => {
        const formGroup = service.createLibrarianFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLibrarian should disable id FormControl', () => {
        const formGroup = service.createLibrarianFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
