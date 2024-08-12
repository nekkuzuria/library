import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBookStorage } from '../book-storage.model';
import { BookStorageService } from '../service/book-storage.service';

const bookStorageResolve = (route: ActivatedRouteSnapshot): Observable<null | IBookStorage> => {
  const id = route.params['id'];
  if (id) {
    return inject(BookStorageService)
      .find(id)
      .pipe(
        mergeMap((bookStorage: HttpResponse<IBookStorage>) => {
          if (bookStorage.body) {
            return of(bookStorage.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default bookStorageResolve;
