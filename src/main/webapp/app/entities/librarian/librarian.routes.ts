import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { LibrarianComponent } from './list/librarian.component';
import { LibrarianDetailComponent } from './detail/librarian-detail.component';
import { LibrarianUpdateComponent } from './update/librarian-update.component';
import LibrarianResolve from './route/librarian-routing-resolve.service';

const librarianRoute: Routes = [
  {
    path: '',
    component: LibrarianComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LibrarianDetailComponent,
    resolve: {
      librarian: LibrarianResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LibrarianUpdateComponent,
    resolve: {
      librarian: LibrarianResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LibrarianUpdateComponent,
    resolve: {
      librarian: LibrarianResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default librarianRoute;
