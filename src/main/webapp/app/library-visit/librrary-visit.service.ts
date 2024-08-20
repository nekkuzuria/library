import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IVisitVM } from './library-visit.model';

export type EntityResponseType = HttpResponse<IVisitVM>;
export type EntityArrayResponseType = HttpResponse<IVisitVM[]>;

@Injectable({ providedIn: 'root' })
export class LibraryVisitService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/visits/my-library');

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IVisitVM[]>(this.resourceUrl, { params: options, observe: 'response' });
  }
}
