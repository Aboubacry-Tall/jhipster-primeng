import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'webrssApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'feed',
    data: { pageTitle: 'webrssApp.feed.home.title' },
    loadChildren: () => import('./feed/feed.routes'),
  },
  {
    path: 'article',
    data: { pageTitle: 'webrssApp.article.home.title' },
    loadChildren: () => import('./article/article.routes'),
  },
  {
    path: 'folder',
    data: { pageTitle: 'webrssApp.folder.home.title' },
    loadChildren: () => import('./folder/folder.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
