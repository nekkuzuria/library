import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IVisitor } from '../visitor.model';
import { VisitorService } from '../service/visitor.service';

const visitorResolve = (route: ActivatedRouteSnapshot): Observable<null | IVisitor> => {
  const id = route.params['id'];
  if (id) {
    return inject(VisitorService)
      .find(id)
      .pipe(
        mergeMap((visitor: HttpResponse<IVisitor>) => {
          if (visitor.body) {
            return of(visitor.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default visitorResolve;
