import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map, Observable } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILibrarian, NewLibrarian } from '../librarian.model';

export type PartialUpdateLibrarian = Partial<ILibrarian> & Pick<ILibrarian, 'id'>;

type RestOf<T extends ILibrarian | NewLibrarian> = Omit<T, 'dateOfBirth'> & {
  dateOfBirth?: string | null;
};

export type RestLibrarian = RestOf<ILibrarian>;

export type NewRestLibrarian = RestOf<NewLibrarian>;

export type PartialUpdateRestLibrarian = RestOf<PartialUpdateLibrarian>;

export type EntityResponseType = HttpResponse<ILibrarian>;
export type EntityArrayResponseType = HttpResponse<ILibrarian[]>;

@Injectable({ providedIn: 'root' })
export class LibrarianService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/librarians');

  create(librarian: NewLibrarian): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(librarian);
    return this.http
      .post<RestLibrarian>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(librarian: ILibrarian): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(librarian);
    return this.http
      .put<RestLibrarian>(`${this.resourceUrl}/${this.getLibrarianIdentifier(librarian)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(librarian: PartialUpdateLibrarian): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(librarian);
    return this.http
      .patch<RestLibrarian>(`${this.resourceUrl}/${this.getLibrarianIdentifier(librarian)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestLibrarian>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestLibrarian[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getLibrarianIdentifier(librarian: Pick<ILibrarian, 'id'>): number {
    return librarian.id;
  }

  compareLibrarian(o1: Pick<ILibrarian, 'id'> | null, o2: Pick<ILibrarian, 'id'> | null): boolean {
    return o1 && o2 ? this.getLibrarianIdentifier(o1) === this.getLibrarianIdentifier(o2) : o1 === o2;
  }

  addLibrarianToCollectionIfMissing<Type extends Pick<ILibrarian, 'id'>>(
    librarianCollection: Type[],
    ...librariansToCheck: (Type | null | undefined)[]
  ): Type[] {
    const librarians: Type[] = librariansToCheck.filter(isPresent);
    if (librarians.length > 0) {
      const librarianCollectionIdentifiers = librarianCollection.map(librarianItem => this.getLibrarianIdentifier(librarianItem));
      const librariansToAdd = librarians.filter(librarianItem => {
        const librarianIdentifier = this.getLibrarianIdentifier(librarianItem);
        if (librarianCollectionIdentifiers.includes(librarianIdentifier)) {
          return false;
        }
        librarianCollectionIdentifiers.push(librarianIdentifier);
        return true;
      });
      return [...librariansToAdd, ...librarianCollection];
    }
    return librarianCollection;
  }

  protected convertDateFromClient<T extends ILibrarian | NewLibrarian | PartialUpdateLibrarian>(librarian: T): RestOf<T> {
    return {
      ...librarian,
      dateOfBirth: librarian.dateOfBirth?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restLibrarian: RestLibrarian): ILibrarian {
    return {
      ...restLibrarian,
      dateOfBirth: restLibrarian.dateOfBirth ? dayjs(restLibrarian.dateOfBirth) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestLibrarian>): HttpResponse<ILibrarian> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestLibrarian[]>): HttpResponse<ILibrarian[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
