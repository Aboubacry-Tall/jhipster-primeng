import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IFeed } from '../feed.model';

@Component({
  selector: 'jhi-feed-detail',
  templateUrl: './feed-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class FeedDetailComponent {
  feed = input<IFeed | null>(null);

  previousState(): void {
    window.history.back();
  }
}
