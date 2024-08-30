import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBookStorage, NewBookStorage } from '../book-storage.model';

export type PartialUpdateBookStorage = Partial<IBookStorage> & Pick<IBookStorage, 'id'>;

export type EntityResponseType = HttpResponse<IBookStorage>;
export type EntityArrayResponseType = HttpResponse<IBookStorage[]>;

@Injectable({ providedIn: 'root' })
export class BookStorageService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/book-storages');

  create(bookStorage: NewBookStorage): Observable<EntityResponseType> {
    return this.http.post<IBookStorage>(this.resourceUrl, bookStorage, { observe: 'response' });
  }

  update(bookStorage: IBookStorage): Observable<EntityResponseType> {
    return this.http.put<IBookStorage>(`${this.resourceUrl}/${this.getBookStorageIdentifier(bookStorage)}`, bookStorage, {
      observe: 'response',
    });
  }

  partialUpdate(bookStorage: PartialUpdateBookStorage): Observable<EntityResponseType> {
    return this.http.patch<IBookStorage>(`${this.resourceUrl}/${this.getBookStorageIdentifier(bookStorage)}`, bookStorage, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IBookStorage>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBookStorage[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getBookStorageIdentifier(bookStorage: Pick<IBookStorage, 'id'>): number {
    return bookStorage.id;
  }

  compareBookStorage(o1: Pick<IBookStorage, 'id'> | null, o2: Pick<IBookStorage, 'id'> | null): boolean {
    return o1 && o2 ? this.getBookStorageIdentifier(o1) === this.getBookStorageIdentifier(o2) : o1 === o2;
  }

  addBookStorageToCollectionIfMissing<Type extends Pick<IBookStorage, 'id'>>(
    bookStorageCollection: Type[],
    ...bookStoragesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const bookStorages: Type[] = bookStoragesToCheck.filter(isPresent);
    if (bookStorages.length > 0) {
      const bookStorageCollectionIdentifiers = bookStorageCollection.map(bookStorageItem => this.getBookStorageIdentifier(bookStorageItem));
      const bookStoragesToAdd = bookStorages.filter(bookStorageItem => {
        const bookStorageIdentifier = this.getBookStorageIdentifier(bookStorageItem);
        if (bookStorageCollectionIdentifiers.includes(bookStorageIdentifier)) {
          return false;
        }
        bookStorageCollectionIdentifiers.push(bookStorageIdentifier);
        return true;
      });
      return [...bookStoragesToAdd, ...bookStorageCollection];
    }
    return bookStorageCollection;
  }
}
