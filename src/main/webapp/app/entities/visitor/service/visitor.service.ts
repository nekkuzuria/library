import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map, Observable } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IVisitor, NewVisitor } from '../visitor.model';

export type PartialUpdateVisitor = Partial<IVisitor> & Pick<IVisitor, 'id'>;

type RestOf<T extends IVisitor | NewVisitor> = Omit<T, 'dateOfBirth'> & {
  dateOfBirth?: string | null;
};

export type RestVisitor = RestOf<IVisitor>;

export type NewRestVisitor = RestOf<NewVisitor>;

export type PartialUpdateRestVisitor = RestOf<PartialUpdateVisitor>;

export type EntityResponseType = HttpResponse<IVisitor>;
export type EntityArrayResponseType = HttpResponse<IVisitor[]>;

@Injectable({ providedIn: 'root' })
export class VisitorService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/visitors');

  create(visitor: NewVisitor): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(visitor);
    return this.http
      .post<RestVisitor>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(visitor: IVisitor): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(visitor);
    return this.http
      .put<RestVisitor>(`${this.resourceUrl}/${this.getVisitorIdentifier(visitor)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(visitor: PartialUpdateVisitor): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(visitor);
    return this.http
      .patch<RestVisitor>(`${this.resourceUrl}/${this.getVisitorIdentifier(visitor)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestVisitor>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestVisitor[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getVisitorIdentifier(visitor: Pick<IVisitor, 'id'>): number {
    return visitor.id;
  }

  compareVisitor(o1: Pick<IVisitor, 'id'> | null, o2: Pick<IVisitor, 'id'> | null): boolean {
    return o1 && o2 ? this.getVisitorIdentifier(o1) === this.getVisitorIdentifier(o2) : o1 === o2;
  }

  getVisitorId(): Observable<number> {
    return this.http.get<number>(this.resourceUrl + '/visitor-id');
  }

  addVisitorToCollectionIfMissing<Type extends Pick<IVisitor, 'id'>>(
    visitorCollection: Type[],
    ...visitorsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const visitors: Type[] = visitorsToCheck.filter(isPresent);
    if (visitors.length > 0) {
      const visitorCollectionIdentifiers = visitorCollection.map(visitorItem => this.getVisitorIdentifier(visitorItem));
      const visitorsToAdd = visitors.filter(visitorItem => {
        const visitorIdentifier = this.getVisitorIdentifier(visitorItem);
        if (visitorCollectionIdentifiers.includes(visitorIdentifier)) {
          return false;
        }
        visitorCollectionIdentifiers.push(visitorIdentifier);
        return true;
      });
      return [...visitorsToAdd, ...visitorCollection];
    }
    return visitorCollection;
  }

  protected convertDateFromClient<T extends IVisitor | NewVisitor | PartialUpdateVisitor>(visitor: T): RestOf<T> {
    return {
      ...visitor,
      dateOfBirth: visitor.dateOfBirth?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restVisitor: RestVisitor): IVisitor {
    return {
      ...restVisitor,
      dateOfBirth: restVisitor.dateOfBirth ? dayjs(restVisitor.dateOfBirth) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestVisitor>): HttpResponse<IVisitor> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestVisitor[]>): HttpResponse<IVisitor[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
