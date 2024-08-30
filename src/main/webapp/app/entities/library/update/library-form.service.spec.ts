import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../library.test-samples';

import { LibraryFormService } from './library-form.service';

describe('Library Form Service', () => {
  let service: LibraryFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LibraryFormService);
  });

  describe('Service methods', () => {
    describe('createLibraryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLibraryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            establishedDate: expect.any(Object),
            location: expect.any(Object),
          }),
        );
      });

      it('passing ILibrary should create a new form with FormGroup', () => {
        const formGroup = service.createLibraryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            establishedDate: expect.any(Object),
            location: expect.any(Object),
          }),
        );
      });
    });

    describe('getLibrary', () => {
      it('should return NewLibrary for default Library initial value', () => {
        const formGroup = service.createLibraryFormGroup(sampleWithNewData);

        const library = service.getLibrary(formGroup) as any;

        expect(library).toMatchObject(sampleWithNewData);
      });

      it('should return NewLibrary for empty Library initial value', () => {
        const formGroup = service.createLibraryFormGroup();

        const library = service.getLibrary(formGroup) as any;

        expect(library).toMatchObject({});
      });

      it('should return ILibrary', () => {
        const formGroup = service.createLibraryFormGroup(sampleWithRequiredData);

        const library = service.getLibrary(formGroup) as any;

        expect(library).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILibrary should not enable id FormControl', () => {
        const formGroup = service.createLibraryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLibrary should disable id FormControl', () => {
        const formGroup = service.createLibraryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
