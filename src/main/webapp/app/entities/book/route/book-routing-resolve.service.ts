import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBook } from '../book.model';
import { BookService } from '../service/book.service';

const bookResolve = (route: ActivatedRouteSnapshot): Observable<null | IBook> => {
  const id = route.params['id'];
  if (id) {
    return inject(BookService)
      .find(id)
      .pipe(
        mergeMap((book: HttpResponse<IBook>) => {
          if (book.body) {
            return of(book.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default bookResolve;
