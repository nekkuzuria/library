import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPendingTaskVM, NewPendingTask } from './pending-task.model';

export type EntityResponseType = HttpResponse<IPendingTaskVM>;
export type EntityArrayResponseType = HttpResponse<IPendingTaskVM[]>;

@Injectable({ providedIn: 'root' })
export class PendingTaskService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/pending-tasks');

  create(book: NewPendingTask): Observable<EntityResponseType> {
    return this.http.post<IPendingTaskVM>(this.resourceUrl, book, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPendingTaskVM[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  updateStatus(taskId: number, status: string): Observable<any> {
    const requestBody = {
      id: taskId,
      status: status,
    };
    return this.http.patch(`${this.resourceUrl}/${taskId}`, requestBody);
  }
}
