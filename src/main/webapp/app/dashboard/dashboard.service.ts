import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { IPersonalStorage } from '../visitor-pages/personal-storage/personal-storage.model';
import { createRequestOption } from 'app/core/request/request-util';
export type EntityArrayResponseType = HttpResponse<IPersonalStorage[]>;

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/visitor-book-storages/my');

  getVisitorBookStorages(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPersonalStorage[]>(this.resourceUrl, { params: options, observe: 'response' });
  }
}
