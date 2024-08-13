import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IVisitorBookStorage } from '../visitor-book-storage.model';
import { VisitorBookStorageService } from '../service/visitor-book-storage.service';

const visitorBookStorageResolve = (route: ActivatedRouteSnapshot): Observable<null | IVisitorBookStorage> => {
  const id = route.params['id'];
  if (id) {
    return inject(VisitorBookStorageService)
      .find(id)
      .pipe(
        mergeMap((visitorBookStorage: HttpResponse<IVisitorBookStorage>) => {
          if (visitorBookStorage.body) {
            return of(visitorBookStorage.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default visitorBookStorageResolve;
