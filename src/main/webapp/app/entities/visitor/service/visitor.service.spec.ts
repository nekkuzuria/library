import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IVisitor } from '../visitor.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../visitor.test-samples';

import { VisitorService, RestVisitor } from './visitor.service';

const requireRestSample: RestVisitor = {
  ...sampleWithRequiredData,
  dateOfBirth: sampleWithRequiredData.dateOfBirth?.format(DATE_FORMAT),
};

describe('Visitor Service', () => {
  let service: VisitorService;
  let httpMock: HttpTestingController;
  let expectedResult: IVisitor | IVisitor[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(VisitorService);
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

    it('should create a Visitor', () => {
      const visitor = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(visitor).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Visitor', () => {
      const visitor = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(visitor).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Visitor', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Visitor', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Visitor', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addVisitorToCollectionIfMissing', () => {
      it('should add a Visitor to an empty array', () => {
        const visitor: IVisitor = sampleWithRequiredData;
        expectedResult = service.addVisitorToCollectionIfMissing([], visitor);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(visitor);
      });

      it('should not add a Visitor to an array that contains it', () => {
        const visitor: IVisitor = sampleWithRequiredData;
        const visitorCollection: IVisitor[] = [
          {
            ...visitor,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addVisitorToCollectionIfMissing(visitorCollection, visitor);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Visitor to an array that doesn't contain it", () => {
        const visitor: IVisitor = sampleWithRequiredData;
        const visitorCollection: IVisitor[] = [sampleWithPartialData];
        expectedResult = service.addVisitorToCollectionIfMissing(visitorCollection, visitor);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(visitor);
      });

      it('should add only unique Visitor to an array', () => {
        const visitorArray: IVisitor[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const visitorCollection: IVisitor[] = [sampleWithRequiredData];
        expectedResult = service.addVisitorToCollectionIfMissing(visitorCollection, ...visitorArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const visitor: IVisitor = sampleWithRequiredData;
        const visitor2: IVisitor = sampleWithPartialData;
        expectedResult = service.addVisitorToCollectionIfMissing([], visitor, visitor2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(visitor);
        expect(expectedResult).toContain(visitor2);
      });

      it('should accept null and undefined values', () => {
        const visitor: IVisitor = sampleWithRequiredData;
        expectedResult = service.addVisitorToCollectionIfMissing([], null, visitor, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(visitor);
      });

      it('should return initial array if no Visitor is added', () => {
        const visitorCollection: IVisitor[] = [sampleWithRequiredData];
        expectedResult = service.addVisitorToCollectionIfMissing(visitorCollection, undefined, null);
        expect(expectedResult).toEqual(visitorCollection);
      });
    });

    describe('compareVisitor', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareVisitor(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareVisitor(entity1, entity2);
        const compareResult2 = service.compareVisitor(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareVisitor(entity1, entity2);
        const compareResult2 = service.compareVisitor(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareVisitor(entity1, entity2);
        const compareResult2 = service.compareVisitor(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
