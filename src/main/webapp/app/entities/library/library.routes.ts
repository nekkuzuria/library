import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { LibraryComponent } from './list/library.component';
import { LibraryDetailComponent } from './detail/library-detail.component';
import { LibraryUpdateComponent } from './update/library-update.component';
import LibraryResolve from './route/library-routing-resolve.service';

const libraryRoute: Routes = [
  {
    path: '',
    component: LibraryComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LibraryDetailComponent,
    resolve: {
      library: LibraryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LibraryUpdateComponent,
    resolve: {
      library: LibraryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LibraryUpdateComponent,
    resolve: {
      library: LibraryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default libraryRoute;
