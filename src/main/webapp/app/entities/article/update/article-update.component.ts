import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IFeed } from 'app/entities/feed/feed.model';
import { FeedService } from 'app/entities/feed/service/feed.service';
import { ArticleService } from '../service/article.service';
import { IArticle } from '../article.model';
import { ArticleFormGroup, ArticleFormService } from './article-form.service';

@Component({
  selector: 'jhi-article-update',
  templateUrl: './article-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ArticleUpdateComponent implements OnInit {
  isSaving = false;
  article: IArticle | null = null;

  feedsSharedCollection: IFeed[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected articleService = inject(ArticleService);
  protected articleFormService = inject(ArticleFormService);
  protected feedService = inject(FeedService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ArticleFormGroup = this.articleFormService.createArticleFormGroup();

  compareFeed = (o1: IFeed | null, o2: IFeed | null): boolean => this.feedService.compareFeed(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ article }) => {
      this.article = article;
      if (article) {
        this.updateForm(article);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('webrssApp.error', { ...err, key: `error.file.${err.key}` })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const article = this.articleFormService.getArticle(this.editForm);
    if (article.id !== null) {
      this.subscribeToSaveResponse(this.articleService.update(article));
    } else {
      this.subscribeToSaveResponse(this.articleService.create(article));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IArticle>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(article: IArticle): void {
    this.article = article;
    this.articleFormService.resetForm(this.editForm, article);

    this.feedsSharedCollection = this.feedService.addFeedToCollectionIfMissing<IFeed>(this.feedsSharedCollection, article.feed);
  }

  protected loadRelationshipsOptions(): void {
    this.feedService
      .query()
      .pipe(map((res: HttpResponse<IFeed[]>) => res.body ?? []))
      .pipe(map((feeds: IFeed[]) => this.feedService.addFeedToCollectionIfMissing<IFeed>(feeds, this.article?.feed)))
      .subscribe((feeds: IFeed[]) => (this.feedsSharedCollection = feeds));
  }
}
