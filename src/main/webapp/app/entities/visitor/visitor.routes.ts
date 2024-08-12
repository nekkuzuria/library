import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { VisitorComponent } from './list/visitor.component';
import { VisitorDetailComponent } from './detail/visitor-detail.component';
import { VisitorUpdateComponent } from './update/visitor-update.component';
import VisitorResolve from './route/visitor-routing-resolve.service';

const visitorRoute: Routes = [
  {
    path: '',
    component: VisitorComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: VisitorDetailComponent,
    resolve: {
      visitor: VisitorResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: VisitorUpdateComponent,
    resolve: {
      visitor: VisitorResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: VisitorUpdateComponent,
    resolve: {
      visitor: VisitorResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default visitorRoute;
