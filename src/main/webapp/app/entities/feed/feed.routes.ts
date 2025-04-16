import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import FeedResolve from './route/feed-routing-resolve.service';

const feedRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/feed.component').then(m => m.FeedComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/feed-detail.component').then(m => m.FeedDetailComponent),
    resolve: {
      feed: FeedResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/feed-update.component').then(m => m.FeedUpdateComponent),
    resolve: {
      feed: FeedResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/feed-update.component').then(m => m.FeedUpdateComponent),
    resolve: {
      feed: FeedResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default feedRoute;
