import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPendingTaskVM, NewPendingTask, NewPendingTaskReturn } from './pending-task.model';
import { PendingTaskStatus } from './pending-task-status.model';

export type EntityResponseType = HttpResponse<IPendingTaskVM>;
export type EntityArrayResponseType = HttpResponse<IPendingTaskVM[]>;

@Injectable({ providedIn: 'root' })
export class PendingTaskService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/pending-tasks');

  create(pt: NewPendingTask): Observable<EntityResponseType> {
    return this.http.post<IPendingTaskVM>(this.resourceUrl, pt, { observe: 'response' });
  }

  createReturn(pt: NewPendingTaskReturn): Observable<EntityResponseType> {
    return this.http.post<IPendingTaskVM>(this.resourceUrl, pt, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPendingTaskVM[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  queryVisitor(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPendingTaskVM[]>(`${this.resourceUrl}/my`, { params: options, observe: 'response' });
  }

  updateStatus(isApproved: boolean, taskId: number, reason: string): Observable<any> {
    const requestBody = {
      id: taskId,
      status: isApproved ? PendingTaskStatus.APPROVED : PendingTaskStatus.DENIED,
      reason: reason,
    };
    if (isApproved) {
      return this.http.patch(`${this.resourceUrl}/${taskId}`, requestBody);
    } else {
      return this.http.patch(`${this.resourceUrl}/${taskId}`, requestBody);
    }
  }
}
