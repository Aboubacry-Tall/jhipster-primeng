import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFeed, NewFeed } from '../feed.model';

export type PartialUpdateFeed = Partial<IFeed> & Pick<IFeed, 'id'>;

type RestOf<T extends IFeed | NewFeed> = Omit<T, 'createdDate' | 'lastFetchedDate'> & {
  createdDate?: string | null;
  lastFetchedDate?: string | null;
};

export type RestFeed = RestOf<IFeed>;

export type NewRestFeed = RestOf<NewFeed>;

export type PartialUpdateRestFeed = RestOf<PartialUpdateFeed>;

export type EntityResponseType = HttpResponse<IFeed>;
export type EntityArrayResponseType = HttpResponse<IFeed[]>;

@Injectable({ providedIn: 'root' })
export class FeedService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/feeds');

  create(feed: NewFeed): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(feed);
    return this.http.post<RestFeed>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(feed: IFeed): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(feed);
    return this.http
      .put<RestFeed>(`${this.resourceUrl}/${this.getFeedIdentifier(feed)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(feed: PartialUpdateFeed): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(feed);
    return this.http
      .patch<RestFeed>(`${this.resourceUrl}/${this.getFeedIdentifier(feed)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestFeed>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestFeed[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getFeedIdentifier(feed: Pick<IFeed, 'id'>): number {
    return feed.id;
  }

  compareFeed(o1: Pick<IFeed, 'id'> | null, o2: Pick<IFeed, 'id'> | null): boolean {
    return o1 && o2 ? this.getFeedIdentifier(o1) === this.getFeedIdentifier(o2) : o1 === o2;
  }

  addFeedToCollectionIfMissing<Type extends Pick<IFeed, 'id'>>(
    feedCollection: Type[],
    ...feedsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const feeds: Type[] = feedsToCheck.filter(isPresent);
    if (feeds.length > 0) {
      const feedCollectionIdentifiers = feedCollection.map(feedItem => this.getFeedIdentifier(feedItem));
      const feedsToAdd = feeds.filter(feedItem => {
        const feedIdentifier = this.getFeedIdentifier(feedItem);
        if (feedCollectionIdentifiers.includes(feedIdentifier)) {
          return false;
        }
        feedCollectionIdentifiers.push(feedIdentifier);
        return true;
      });
      return [...feedsToAdd, ...feedCollection];
    }
    return feedCollection;
  }

  protected convertDateFromClient<T extends IFeed | NewFeed | PartialUpdateFeed>(feed: T): RestOf<T> {
    return {
      ...feed,
      createdDate: feed.createdDate?.toJSON() ?? null,
      lastFetchedDate: feed.lastFetchedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restFeed: RestFeed): IFeed {
    return {
      ...restFeed,
      createdDate: restFeed.createdDate ? dayjs(restFeed.createdDate) : undefined,
      lastFetchedDate: restFeed.lastFetchedDate ? dayjs(restFeed.lastFetchedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestFeed>): HttpResponse<IFeed> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestFeed[]>): HttpResponse<IFeed[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
