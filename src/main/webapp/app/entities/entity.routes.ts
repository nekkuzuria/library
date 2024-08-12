import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'Authorities' },
    loadChildren: () => import('./admin/authority/authority.routes'),
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
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
