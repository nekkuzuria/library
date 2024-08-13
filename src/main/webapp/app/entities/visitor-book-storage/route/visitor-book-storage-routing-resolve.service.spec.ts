import { TestBed } from '@angular/core/testing';
import { provideHttpClient, HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';

import { IVisitorBookStorage } from '../visitor-book-storage.model';
import { VisitorBookStorageService } from '../service/visitor-book-storage.service';

import visitorBookStorageResolve from './visitor-book-storage-routing-resolve.service';

describe('VisitorBookStorage routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: VisitorBookStorageService;
  let resultVisitorBookStorage: IVisitorBookStorage | null | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    service = TestBed.inject(VisitorBookStorageService);
    resultVisitorBookStorage = undefined;
  });

  describe('resolve', () => {
    it('should return IVisitorBookStorage returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        visitorBookStorageResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultVisitorBookStorage = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123);
      expect(resultVisitorBookStorage).toEqual({ id: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        visitorBookStorageResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultVisitorBookStorage = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultVisitorBookStorage).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IVisitorBookStorage>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        visitorBookStorageResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultVisitorBookStorage = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123);
      expect(resultVisitorBookStorage).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
