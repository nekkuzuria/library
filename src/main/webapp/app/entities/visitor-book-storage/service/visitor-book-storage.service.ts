import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map, Observable } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IVisitorBookStorage, NewVisitorBookStorage } from '../visitor-book-storage.model';

export type PartialUpdateVisitorBookStorage = Partial<IVisitorBookStorage> & Pick<IVisitorBookStorage, 'id'>;

type RestOf<T extends IVisitorBookStorage | NewVisitorBookStorage> = Omit<T, 'borrowDate' | 'returnDate'> & {
  borrowDate?: string | null;
  returnDate?: string | null;
};

export type RestVisitorBookStorage = RestOf<IVisitorBookStorage>;

export type NewRestVisitorBookStorage = RestOf<NewVisitorBookStorage>;

export type PartialUpdateRestVisitorBookStorage = RestOf<PartialUpdateVisitorBookStorage>;

export type EntityResponseType = HttpResponse<IVisitorBookStorage>;
export type EntityArrayResponseType = HttpResponse<IVisitorBookStorage[]>;

@Injectable({ providedIn: 'root' })
export class VisitorBookStorageService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/visitor-book-storages');

  create(visitorBookStorage: NewVisitorBookStorage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(visitorBookStorage);
    return this.http
      .post<RestVisitorBookStorage>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(visitorBookStorage: IVisitorBookStorage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(visitorBookStorage);
    return this.http
      .put<RestVisitorBookStorage>(`${this.resourceUrl}/${this.getVisitorBookStorageIdentifier(visitorBookStorage)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(visitorBookStorage: PartialUpdateVisitorBookStorage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(visitorBookStorage);
    return this.http
      .patch<RestVisitorBookStorage>(`${this.resourceUrl}/${this.getVisitorBookStorageIdentifier(visitorBookStorage)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestVisitorBookStorage>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestVisitorBookStorage[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getVisitorBookStorageIdentifier(visitorBookStorage: Pick<IVisitorBookStorage, 'id'>): number {
    return visitorBookStorage.id;
  }

  compareVisitorBookStorage(o1: Pick<IVisitorBookStorage, 'id'> | null, o2: Pick<IVisitorBookStorage, 'id'> | null): boolean {
    return o1 && o2 ? this.getVisitorBookStorageIdentifier(o1) === this.getVisitorBookStorageIdentifier(o2) : o1 === o2;
  }

  addVisitorBookStorageToCollectionIfMissing<Type extends Pick<IVisitorBookStorage, 'id'>>(
    visitorBookStorageCollection: Type[],
    ...visitorBookStoragesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const visitorBookStorages: Type[] = visitorBookStoragesToCheck.filter(isPresent);
    if (visitorBookStorages.length > 0) {
      const visitorBookStorageCollectionIdentifiers = visitorBookStorageCollection.map(visitorBookStorageItem =>
        this.getVisitorBookStorageIdentifier(visitorBookStorageItem),
      );
      const visitorBookStoragesToAdd = visitorBookStorages.filter(visitorBookStorageItem => {
        const visitorBookStorageIdentifier = this.getVisitorBookStorageIdentifier(visitorBookStorageItem);
        if (visitorBookStorageCollectionIdentifiers.includes(visitorBookStorageIdentifier)) {
          return false;
        }
        visitorBookStorageCollectionIdentifiers.push(visitorBookStorageIdentifier);
        return true;
      });
      return [...visitorBookStoragesToAdd, ...visitorBookStorageCollection];
    }
    return visitorBookStorageCollection;
  }

  protected convertDateFromClient<T extends IVisitorBookStorage | NewVisitorBookStorage | PartialUpdateVisitorBookStorage>(
    visitorBookStorage: T,
  ): RestOf<T> {
    return {
      ...visitorBookStorage,
      borrowDate: visitorBookStorage.borrowDate?.format(DATE_FORMAT) ?? null,
      returnDate: visitorBookStorage.returnDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restVisitorBookStorage: RestVisitorBookStorage): IVisitorBookStorage {
    return {
      ...restVisitorBookStorage,
      borrowDate: restVisitorBookStorage.borrowDate ? dayjs(restVisitorBookStorage.borrowDate) : undefined,
      returnDate: restVisitorBookStorage.returnDate ? dayjs(restVisitorBookStorage.returnDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestVisitorBookStorage>): HttpResponse<IVisitorBookStorage> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestVisitorBookStorage[]>): HttpResponse<IVisitorBookStorage[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
