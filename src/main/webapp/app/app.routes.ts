import { Routes } from '@angular/router';

import { Authority } from 'app/config/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { errorRoute } from './layouts/error/error.route';

import HomeComponent from './home/home.component';
import NavbarComponent from './layouts/navbar/navbar.component';
import LoginComponent from './login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { BookDetailComponent } from './book-detail/book-detail.component';
import { PersonalStorageComponent } from './visitor-pages/personal-storage/personal-storage.component';
import { LibraryVisitComponent } from './library-visit/library-visit.component';
import { PendingTaskComponent } from './pending-task/pending-task.component';
import { BorrowHistoryComponent } from './visitor-pages/borrow-history/borrow-history.component';
import { MyRequestsComponent } from './visitor-pages/my-requests/my-requests.component';

const routes: Routes = [
  {
    path: '',
    component: DashboardComponent,
    title: 'dashboard.title',
    canActivate: [UserRouteAccessService],
  },
  {
    path: '',
    component: NavbarComponent,
    outlet: 'navbar',
  },
  {
    path: 'admin',
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./admin/admin.routes'),
  },
  {
    path: 'account',
    loadChildren: () => import('./account/account.route'),
  },
  {
    path: 'login',
    component: LoginComponent,
    title: 'login.title',
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    title: 'dashboard.title',
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'book-detail/:id',
    component: BookDetailComponent,
    title: 'book-detail.title',
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'my-books',
    component: PersonalStorageComponent,
    title: 'My Books',
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'borrow-history',
    component: BorrowHistoryComponent,
    title: 'Borrow History',
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'my-requests',
    component: MyRequestsComponent,
    title: 'My Requests',
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'library-visit',
    component: LibraryVisitComponent,
    title: 'library-visit.title',
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'pending-tasks',
    component: PendingTaskComponent,
    title: 'Pending Tasks',
    canActivate: [UserRouteAccessService],
  },
  {
    path: '',
    loadChildren: () => import(`./entities/entity.routes`),
  },
  ...errorRoute,
];

export default routes;
