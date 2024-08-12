import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { BookComponent } from './list/book.component';
import { BookDetailComponent } from './detail/book-detail.component';
import { BookUpdateComponent } from './update/book-update.component';
import BookResolve from './route/book-routing-resolve.service';

const bookRoute: Routes = [
  {
    path: '',
    component: BookComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BookDetailComponent,
    resolve: {
      book: BookResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BookUpdateComponent,
    resolve: {
      book: BookResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BookUpdateComponent,
    resolve: {
      book: BookResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default bookRoute;
