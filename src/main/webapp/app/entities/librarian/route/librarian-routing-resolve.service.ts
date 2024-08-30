import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILibrarian } from '../librarian.model';
import { LibrarianService } from '../service/librarian.service';

const librarianResolve = (route: ActivatedRouteSnapshot): Observable<null | ILibrarian> => {
  const id = route.params['id'];
  if (id) {
    return inject(LibrarianService)
      .find(id)
      .pipe(
        mergeMap((librarian: HttpResponse<ILibrarian>) => {
          if (librarian.body) {
            return of(librarian.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default librarianResolve;
