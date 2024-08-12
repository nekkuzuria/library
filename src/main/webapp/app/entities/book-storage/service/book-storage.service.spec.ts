import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IBookStorage } from '../book-storage.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../book-storage.test-samples';

import { BookStorageService } from './book-storage.service';

const requireRestSample: IBookStorage = {
  ...sampleWithRequiredData,
};

describe('BookStorage Service', () => {
  let service: BookStorageService;
  let httpMock: HttpTestingController;
  let expectedResult: IBookStorage | IBookStorage[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(BookStorageService);
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

    it('should create a BookStorage', () => {
      const bookStorage = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(bookStorage).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a BookStorage', () => {
      const bookStorage = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(bookStorage).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a BookStorage', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of BookStorage', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a BookStorage', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addBookStorageToCollectionIfMissing', () => {
      it('should add a BookStorage to an empty array', () => {
        const bookStorage: IBookStorage = sampleWithRequiredData;
        expectedResult = service.addBookStorageToCollectionIfMissing([], bookStorage);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(bookStorage);
      });

      it('should not add a BookStorage to an array that contains it', () => {
        const bookStorage: IBookStorage = sampleWithRequiredData;
        const bookStorageCollection: IBookStorage[] = [
          {
            ...bookStorage,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBookStorageToCollectionIfMissing(bookStorageCollection, bookStorage);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a BookStorage to an array that doesn't contain it", () => {
        const bookStorage: IBookStorage = sampleWithRequiredData;
        const bookStorageCollection: IBookStorage[] = [sampleWithPartialData];
        expectedResult = service.addBookStorageToCollectionIfMissing(bookStorageCollection, bookStorage);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(bookStorage);
      });

      it('should add only unique BookStorage to an array', () => {
        const bookStorageArray: IBookStorage[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const bookStorageCollection: IBookStorage[] = [sampleWithRequiredData];
        expectedResult = service.addBookStorageToCollectionIfMissing(bookStorageCollection, ...bookStorageArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const bookStorage: IBookStorage = sampleWithRequiredData;
        const bookStorage2: IBookStorage = sampleWithPartialData;
        expectedResult = service.addBookStorageToCollectionIfMissing([], bookStorage, bookStorage2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(bookStorage);
        expect(expectedResult).toContain(bookStorage2);
      });

      it('should accept null and undefined values', () => {
        const bookStorage: IBookStorage = sampleWithRequiredData;
        expectedResult = service.addBookStorageToCollectionIfMissing([], null, bookStorage, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(bookStorage);
      });

      it('should return initial array if no BookStorage is added', () => {
        const bookStorageCollection: IBookStorage[] = [sampleWithRequiredData];
        expectedResult = service.addBookStorageToCollectionIfMissing(bookStorageCollection, undefined, null);
        expect(expectedResult).toEqual(bookStorageCollection);
      });
    });

    describe('compareBookStorage', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBookStorage(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareBookStorage(entity1, entity2);
        const compareResult2 = service.compareBookStorage(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareBookStorage(entity1, entity2);
        const compareResult2 = service.compareBookStorage(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareBookStorage(entity1, entity2);
        const compareResult2 = service.compareBookStorage(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
