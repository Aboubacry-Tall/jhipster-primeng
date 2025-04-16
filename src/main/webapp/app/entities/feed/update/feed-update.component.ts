import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IFeed } from '../feed.model';
import { FeedService } from '../service/feed.service';
import { FeedFormGroup, FeedFormService } from './feed-form.service';

@Component({
  selector: 'jhi-feed-update',
  templateUrl: './feed-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class FeedUpdateComponent implements OnInit {
  isSaving = false;
  feed: IFeed | null = null;

  protected feedService = inject(FeedService);
  protected feedFormService = inject(FeedFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: FeedFormGroup = this.feedFormService.createFeedFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ feed }) => {
      this.feed = feed;
      if (feed) {
        this.updateForm(feed);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const feed = this.feedFormService.getFeed(this.editForm);
    if (feed.id !== null) {
      this.subscribeToSaveResponse(this.feedService.update(feed));
    } else {
      this.subscribeToSaveResponse(this.feedService.create(feed));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFeed>>): void {
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

  protected updateForm(feed: IFeed): void {
    this.feed = feed;
    this.feedFormService.resetForm(this.editForm, feed);
  }
}
