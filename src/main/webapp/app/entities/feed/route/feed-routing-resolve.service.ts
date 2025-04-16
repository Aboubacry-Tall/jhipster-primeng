import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFeed } from '../feed.model';
import { FeedService } from '../service/feed.service';

const feedResolve = (route: ActivatedRouteSnapshot): Observable<null | IFeed> => {
  const id = route.params.id;
  if (id) {
    return inject(FeedService)
      .find(id)
      .pipe(
        mergeMap((feed: HttpResponse<IFeed>) => {
          if (feed.body) {
            return of(feed.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default feedResolve;
