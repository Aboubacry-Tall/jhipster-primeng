import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { FeedDetailComponent } from './feed-detail.component';

describe('Feed Management Detail Component', () => {
  let comp: FeedDetailComponent;
  let fixture: ComponentFixture<FeedDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FeedDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./feed-detail.component').then(m => m.FeedDetailComponent),
              resolve: { feed: () => of({ id: 12331 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(FeedDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FeedDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load feed on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', FeedDetailComponent);

      // THEN
      expect(instance.feed()).toEqual(expect.objectContaining({ id: 12331 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
