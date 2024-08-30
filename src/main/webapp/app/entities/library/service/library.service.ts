import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map, Observable } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILibrary, NewLibrary } from '../library.model';

export type PartialUpdateLibrary = Partial<ILibrary> & Pick<ILibrary, 'id'>;

type RestOf<T extends ILibrary | NewLibrary> = Omit<T, 'establishedDate'> & {
  establishedDate?: string | null;
};

export type RestLibrary = RestOf<ILibrary>;

export type NewRestLibrary = RestOf<NewLibrary>;

export type PartialUpdateRestLibrary = RestOf<PartialUpdateLibrary>;

export type EntityResponseType = HttpResponse<ILibrary>;
export type EntityArrayResponseType = HttpResponse<ILibrary[]>;

@Injectable({ providedIn: 'root' })
export class LibraryService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/libraries');

  create(library: NewLibrary): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(library);
    return this.http
      .post<RestLibrary>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(library: ILibrary): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(library);
    return this.http
      .put<RestLibrary>(`${this.resourceUrl}/${this.getLibraryIdentifier(library)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(library: PartialUpdateLibrary): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(library);
    return this.http
      .patch<RestLibrary>(`${this.resourceUrl}/${this.getLibraryIdentifier(library)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestLibrary>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestLibrary[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getLibraryIdentifier(library: Pick<ILibrary, 'id'>): number {
    return library.id;
  }

  compareLibrary(o1: Pick<ILibrary, 'id'> | null, o2: Pick<ILibrary, 'id'> | null): boolean {
    return o1 && o2 ? this.getLibraryIdentifier(o1) === this.getLibraryIdentifier(o2) : o1 === o2;
  }

  addLibraryToCollectionIfMissing<Type extends Pick<ILibrary, 'id'>>(
    libraryCollection: Type[],
    ...librariesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const libraries: Type[] = librariesToCheck.filter(isPresent);
    if (libraries.length > 0) {
      const libraryCollectionIdentifiers = libraryCollection.map(libraryItem => this.getLibraryIdentifier(libraryItem));
      const librariesToAdd = libraries.filter(libraryItem => {
        const libraryIdentifier = this.getLibraryIdentifier(libraryItem);
        if (libraryCollectionIdentifiers.includes(libraryIdentifier)) {
          return false;
        }
        libraryCollectionIdentifiers.push(libraryIdentifier);
        return true;
      });
      return [...librariesToAdd, ...libraryCollection];
    }
    return libraryCollection;
  }

  protected convertDateFromClient<T extends ILibrary | NewLibrary | PartialUpdateLibrary>(library: T): RestOf<T> {
    return {
      ...library,
      establishedDate: library.establishedDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restLibrary: RestLibrary): ILibrary {
    return {
      ...restLibrary,
      establishedDate: restLibrary.establishedDate ? dayjs(restLibrary.establishedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestLibrary>): HttpResponse<ILibrary> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestLibrary[]>): HttpResponse<ILibrary[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
