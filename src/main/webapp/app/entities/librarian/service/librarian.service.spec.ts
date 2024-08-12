import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ILibrarian } from '../librarian.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../librarian.test-samples';

import { LibrarianService, RestLibrarian } from './librarian.service';

const requireRestSample: RestLibrarian = {
  ...sampleWithRequiredData,
  dateOfBirth: sampleWithRequiredData.dateOfBirth?.format(DATE_FORMAT),
};

describe('Librarian Service', () => {
  let service: LibrarianService;
  let httpMock: HttpTestingController;
  let expectedResult: ILibrarian | ILibrarian[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(LibrarianService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Librarian', () => {
      const librarian = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(librarian).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Librarian', () => {
      const librarian = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(librarian).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Librarian', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Librarian', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Librarian', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addLibrarianToCollectionIfMissing', () => {
      it('should add a Librarian to an empty array', () => {
        const librarian: ILibrarian = sampleWithRequiredData;
        expectedResult = service.addLibrarianToCollectionIfMissing([], librarian);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(librarian);
      });

      it('should not add a Librarian to an array that contains it', () => {
        const librarian: ILibrarian = sampleWithRequiredData;
        const librarianCollection: ILibrarian[] = [
          {
            ...librarian,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addLibrarianToCollectionIfMissing(librarianCollection, librarian);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Librarian to an array that doesn't contain it", () => {
        const librarian: ILibrarian = sampleWithRequiredData;
        const librarianCollection: ILibrarian[] = [sampleWithPartialData];
        expectedResult = service.addLibrarianToCollectionIfMissing(librarianCollection, librarian);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(librarian);
      });

      it('should add only unique Librarian to an array', () => {
        const librarianArray: ILibrarian[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const librarianCollection: ILibrarian[] = [sampleWithRequiredData];
        expectedResult = service.addLibrarianToCollectionIfMissing(librarianCollection, ...librarianArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const librarian: ILibrarian = sampleWithRequiredData;
        const librarian2: ILibrarian = sampleWithPartialData;
        expectedResult = service.addLibrarianToCollectionIfMissing([], librarian, librarian2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(librarian);
        expect(expectedResult).toContain(librarian2);
      });

      it('should accept null and undefined values', () => {
        const librarian: ILibrarian = sampleWithRequiredData;
        expectedResult = service.addLibrarianToCollectionIfMissing([], null, librarian, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(librarian);
      });

      it('should return initial array if no Librarian is added', () => {
        const librarianCollection: ILibrarian[] = [sampleWithRequiredData];
        expectedResult = service.addLibrarianToCollectionIfMissing(librarianCollection, undefined, null);
        expect(expectedResult).toEqual(librarianCollection);
      });
    });

    describe('compareLibrarian', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareLibrarian(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareLibrarian(entity1, entity2);
        const compareResult2 = service.compareLibrarian(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareLibrarian(entity1, entity2);
        const compareResult2 = service.compareLibrarian(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareLibrarian(entity1, entity2);
        const compareResult2 = service.compareLibrarian(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
