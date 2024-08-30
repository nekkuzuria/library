import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { VisitorBookStorageComponent } from './list/visitor-book-storage.component';
import { VisitorBookStorageDetailComponent } from './detail/visitor-book-storage-detail.component';
import { VisitorBookStorageUpdateComponent } from './update/visitor-book-storage-update.component';
import VisitorBookStorageResolve from './route/visitor-book-storage-routing-resolve.service';

const visitorBookStorageRoute: Routes = [
  {
    path: '',
    component: VisitorBookStorageComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: VisitorBookStorageDetailComponent,
    resolve: {
      visitorBookStorage: VisitorBookStorageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: VisitorBookStorageUpdateComponent,
    resolve: {
      visitorBookStorage: VisitorBookStorageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: VisitorBookStorageUpdateComponent,
    resolve: {
      visitorBookStorage: VisitorBookStorageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default visitorBookStorageRoute;
