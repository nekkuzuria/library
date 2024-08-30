import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFile, NewFile } from '../file.model';

export type PartialUpdateFile = Partial<IFile> & Pick<IFile, 'id'>;

export type EntityResponseType = HttpResponse<IFile>;
export type EntityArrayResponseType = HttpResponse<IFile[]>;

@Injectable({ providedIn: 'root' })
export class FileService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/files');

  create(file: NewFile): Observable<EntityResponseType> {
    return this.http.post<IFile>(this.resourceUrl, file, { observe: 'response' });
  }

  update(file: IFile): Observable<EntityResponseType> {
    return this.http.put<IFile>(`${this.resourceUrl}/${this.getFileIdentifier(file)}`, file, { observe: 'response' });
  }

  partialUpdate(file: PartialUpdateFile): Observable<EntityResponseType> {
    return this.http.patch<IFile>(`${this.resourceUrl}/${this.getFileIdentifier(file)}`, file, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IFile>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFile[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getFileIdentifier(file: Pick<IFile, 'id'>): number {
    return file.id;
  }

  compareFile(o1: Pick<IFile, 'id'> | null, o2: Pick<IFile, 'id'> | null): boolean {
    return o1 && o2 ? this.getFileIdentifier(o1) === this.getFileIdentifier(o2) : o1 === o2;
  }

  addFileToCollectionIfMissing<Type extends Pick<IFile, 'id'>>(
    fileCollection: Type[],
    ...filesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const files: Type[] = filesToCheck.filter(isPresent);
    if (files.length > 0) {
      const fileCollectionIdentifiers = fileCollection.map(fileItem => this.getFileIdentifier(fileItem));
      const filesToAdd = files.filter(fileItem => {
        const fileIdentifier = this.getFileIdentifier(fileItem);
        if (fileCollectionIdentifiers.includes(fileIdentifier)) {
          return false;
        }
        fileCollectionIdentifiers.push(fileIdentifier);
        return true;
      });
      return [...filesToAdd, ...fileCollection];
    }
    return fileCollection;
  }
}
