import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IFeed, NewFeed } from '../feed.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFeed for edit and NewFeedFormGroupInput for create.
 */
type FeedFormGroupInput = IFeed | PartialWithRequiredKeyOf<NewFeed>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IFeed | NewFeed> = Omit<T, 'createdDate' | 'lastFetchedDate'> & {
  createdDate?: string | null;
  lastFetchedDate?: string | null;
};

type FeedFormRawValue = FormValueOf<IFeed>;

type NewFeedFormRawValue = FormValueOf<NewFeed>;

type FeedFormDefaults = Pick<NewFeed, 'id' | 'autoDiscovered' | 'active' | 'createdDate' | 'lastFetchedDate'>;

type FeedFormGroupContent = {
  id: FormControl<FeedFormRawValue['id'] | NewFeed['id']>;
  name: FormControl<FeedFormRawValue['name']>;
  url: FormControl<FeedFormRawValue['url']>;
  siteName: FormControl<FeedFormRawValue['siteName']>;
  description: FormControl<FeedFormRawValue['description']>;
  category: FormControl<FeedFormRawValue['category']>;
  autoDiscovered: FormControl<FeedFormRawValue['autoDiscovered']>;
  faviconUrl: FormControl<FeedFormRawValue['faviconUrl']>;
  active: FormControl<FeedFormRawValue['active']>;
  createdDate: FormControl<FeedFormRawValue['createdDate']>;
  lastFetchedDate: FormControl<FeedFormRawValue['lastFetchedDate']>;
};

export type FeedFormGroup = FormGroup<FeedFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FeedFormService {
  createFeedFormGroup(feed: FeedFormGroupInput = { id: null }): FeedFormGroup {
    const feedRawValue = this.convertFeedToFeedRawValue({
      ...this.getFormDefaults(),
      ...feed,
    });
    return new FormGroup<FeedFormGroupContent>({
      id: new FormControl(
        { value: feedRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(feedRawValue.name, {
        validators: [Validators.required],
      }),
      url: new FormControl(feedRawValue.url, {
        validators: [Validators.required],
      }),
      siteName: new FormControl(feedRawValue.siteName),
      description: new FormControl(feedRawValue.description),
      category: new FormControl(feedRawValue.category),
      autoDiscovered: new FormControl(feedRawValue.autoDiscovered),
      faviconUrl: new FormControl(feedRawValue.faviconUrl),
      active: new FormControl(feedRawValue.active),
      createdDate: new FormControl(feedRawValue.createdDate),
      lastFetchedDate: new FormControl(feedRawValue.lastFetchedDate),
    });
  }

  getFeed(form: FeedFormGroup): IFeed | NewFeed {
    return this.convertFeedRawValueToFeed(form.getRawValue() as FeedFormRawValue | NewFeedFormRawValue);
  }

  resetForm(form: FeedFormGroup, feed: FeedFormGroupInput): void {
    const feedRawValue = this.convertFeedToFeedRawValue({ ...this.getFormDefaults(), ...feed });
    form.reset(
      {
        ...feedRawValue,
        id: { value: feedRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): FeedFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      autoDiscovered: false,
      active: false,
      createdDate: currentTime,
      lastFetchedDate: currentTime,
    };
  }

  private convertFeedRawValueToFeed(rawFeed: FeedFormRawValue | NewFeedFormRawValue): IFeed | NewFeed {
    return {
      ...rawFeed,
      createdDate: dayjs(rawFeed.createdDate, DATE_TIME_FORMAT),
      lastFetchedDate: dayjs(rawFeed.lastFetchedDate, DATE_TIME_FORMAT),
    };
  }

  private convertFeedToFeedRawValue(
    feed: IFeed | (Partial<NewFeed> & FeedFormDefaults),
  ): FeedFormRawValue | PartialWithRequiredKeyOf<NewFeedFormRawValue> {
    return {
      ...feed,
      createdDate: feed.createdDate ? feed.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastFetchedDate: feed.lastFetchedDate ? feed.lastFetchedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
