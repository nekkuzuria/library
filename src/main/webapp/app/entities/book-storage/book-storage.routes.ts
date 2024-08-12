import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { BookStorageComponent } from './list/book-storage.component';
import { BookStorageDetailComponent } from './detail/book-storage-detail.component';
import { BookStorageUpdateComponent } from './update/book-storage-update.component';
import BookStorageResolve from './route/book-storage-routing-resolve.service';

const bookStorageRoute: Routes = [
  {
    path: '',
    component: BookStorageComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BookStorageDetailComponent,
    resolve: {
      bookStorage: BookStorageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BookStorageUpdateComponent,
    resolve: {
      bookStorage: BookStorageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BookStorageUpdateComponent,
    resolve: {
      bookStorage: BookStorageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default bookStorageRoute;
