import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IArticle, NewArticle } from '../article.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IArticle for edit and NewArticleFormGroupInput for create.
 */
type ArticleFormGroupInput = IArticle | PartialWithRequiredKeyOf<NewArticle>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IArticle | NewArticle> = Omit<T, 'publishedDate'> & {
  publishedDate?: string | null;
};

type ArticleFormRawValue = FormValueOf<IArticle>;

type NewArticleFormRawValue = FormValueOf<NewArticle>;

type ArticleFormDefaults = Pick<NewArticle, 'id' | 'publishedDate' | 'read' | 'favorited'>;

type ArticleFormGroupContent = {
  id: FormControl<ArticleFormRawValue['id'] | NewArticle['id']>;
  title: FormControl<ArticleFormRawValue['title']>;
  link: FormControl<ArticleFormRawValue['link']>;
  content: FormControl<ArticleFormRawValue['content']>;
  summary: FormControl<ArticleFormRawValue['summary']>;
  author: FormControl<ArticleFormRawValue['author']>;
  publishedDate: FormControl<ArticleFormRawValue['publishedDate']>;
  read: FormControl<ArticleFormRawValue['read']>;
  favorited: FormControl<ArticleFormRawValue['favorited']>;
  tags: FormControl<ArticleFormRawValue['tags']>;
  coverImageUrl: FormControl<ArticleFormRawValue['coverImageUrl']>;
  views: FormControl<ArticleFormRawValue['views']>;
  feed: FormControl<ArticleFormRawValue['feed']>;
};

export type ArticleFormGroup = FormGroup<ArticleFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ArticleFormService {
  createArticleFormGroup(article: ArticleFormGroupInput = { id: null }): ArticleFormGroup {
    const articleRawValue = this.convertArticleToArticleRawValue({
      ...this.getFormDefaults(),
      ...article,
    });
    return new FormGroup<ArticleFormGroupContent>({
      id: new FormControl(
        { value: articleRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(articleRawValue.title, {
        validators: [Validators.required],
      }),
      link: new FormControl(articleRawValue.link, {
        validators: [Validators.required],
      }),
      content: new FormControl(articleRawValue.content),
      summary: new FormControl(articleRawValue.summary),
      author: new FormControl(articleRawValue.author),
      publishedDate: new FormControl(articleRawValue.publishedDate),
      read: new FormControl(articleRawValue.read),
      favorited: new FormControl(articleRawValue.favorited),
      tags: new FormControl(articleRawValue.tags),
      coverImageUrl: new FormControl(articleRawValue.coverImageUrl),
      views: new FormControl(articleRawValue.views),
      feed: new FormControl(articleRawValue.feed),
    });
  }

  getArticle(form: ArticleFormGroup): IArticle | NewArticle {
    return this.convertArticleRawValueToArticle(form.getRawValue() as ArticleFormRawValue | NewArticleFormRawValue);
  }

  resetForm(form: ArticleFormGroup, article: ArticleFormGroupInput): void {
    const articleRawValue = this.convertArticleToArticleRawValue({ ...this.getFormDefaults(), ...article });
    form.reset(
      {
        ...articleRawValue,
        id: { value: articleRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ArticleFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      publishedDate: currentTime,
      read: false,
      favorited: false,
    };
  }

  private convertArticleRawValueToArticle(rawArticle: ArticleFormRawValue | NewArticleFormRawValue): IArticle | NewArticle {
    return {
      ...rawArticle,
      publishedDate: dayjs(rawArticle.publishedDate, DATE_TIME_FORMAT),
    };
  }

  private convertArticleToArticleRawValue(
    article: IArticle | (Partial<NewArticle> & ArticleFormDefaults),
  ): ArticleFormRawValue | PartialWithRequiredKeyOf<NewArticleFormRawValue> {
    return {
      ...article,
      publishedDate: article.publishedDate ? article.publishedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
