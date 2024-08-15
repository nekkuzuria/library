import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map, Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { IPublicLibrary } from './public.library.model';
import { createRequestOption } from 'app/core/request/request-util';

type RestOf<T extends IPublicLibrary> = Omit<T, 'establishedDate'> & {
  establishedDate?: string | null;
};

export type RestLibrary = RestOf<IPublicLibrary>;

export type EntityResponseType = HttpResponse<IPublicLibrary>;
export type EntityArrayResponseType = HttpResponse<IPublicLibrary[]>;

@Injectable({
  providedIn: 'root',
})
export class PublicService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/public/libraries');

  constructor() {}
  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestLibrary[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestLibrary[]>): HttpResponse<IPublicLibrary[]> {
    return res.clone({
      body: res.body ? res.body : null,
    });
  }
}
