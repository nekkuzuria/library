import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILibrary } from '../library.model';
import { LibraryService } from '../service/library.service';

const libraryResolve = (route: ActivatedRouteSnapshot): Observable<null | ILibrary> => {
  const id = route.params['id'];
  if (id) {
    return inject(LibraryService)
      .find(id)
      .pipe(
        mergeMap((library: HttpResponse<ILibrary>) => {
          if (library.body) {
            return of(library.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default libraryResolve;
