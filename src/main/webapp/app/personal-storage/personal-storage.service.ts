import { IPersonalStorage } from './personal-storage.model';
import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';

export type EntityResponseType = HttpResponse<IPersonalStorage>;
export type EntityArrayResponseType = HttpResponse<IPersonalStorage[]>;

@Injectable({ providedIn: 'root' })
export class PersonalStorageService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/visitor-book-storages/my');

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPersonalStorage[]>(this.resourceUrl, { params: options, observe: 'response' });
  }
}
