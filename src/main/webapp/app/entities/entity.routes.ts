import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'Authorities' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'library',
    data: { pageTitle: 'Libraries' },
    loadChildren: () => import('./library/library.routes'),
  },
  {
    path: 'location',
    data: { pageTitle: 'Locations' },
    loadChildren: () => import('./location/location.routes'),
  },
  {
    path: 'visitor',
    data: { pageTitle: 'Visitors' },
    loadChildren: () => import('./visitor/visitor.routes'),
  },
  {
    path: 'librarian',
    data: { pageTitle: 'Librarians' },
    loadChildren: () => import('./librarian/librarian.routes'),
  },
  {
    path: 'book',
    data: { pageTitle: 'Books' },
    loadChildren: () => import('./book/book.routes'),
  },
  {
    path: 'book-storage',
    data: { pageTitle: 'BookStorages' },
    loadChildren: () => import('./book-storage/book-storage.routes'),
  },
  {
    path: 'visitor-book-storage',
    data: { pageTitle: 'VisitorBookStorages' },
    loadChildren: () => import('./visitor-book-storage/visitor-book-storage.routes'),
  },
  {
    path: 'visit',
    data: { pageTitle: 'Visits' },
    loadChildren: () => import('./visit/visit.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
