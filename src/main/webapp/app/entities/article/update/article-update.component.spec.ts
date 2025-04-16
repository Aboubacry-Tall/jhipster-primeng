import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IFeed } from 'app/entities/feed/feed.model';
import { FeedService } from 'app/entities/feed/service/feed.service';
import { ArticleService } from '../service/article.service';
import { IArticle } from '../article.model';
import { ArticleFormService } from './article-form.service';

import { ArticleUpdateComponent } from './article-update.component';

describe('Article Management Update Component', () => {
  let comp: ArticleUpdateComponent;
  let fixture: ComponentFixture<ArticleUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let articleFormService: ArticleFormService;
  let articleService: ArticleService;
  let feedService: FeedService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ArticleUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ArticleUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ArticleUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    articleFormService = TestBed.inject(ArticleFormService);
    articleService = TestBed.inject(ArticleService);
    feedService = TestBed.inject(FeedService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Feed query and add missing value', () => {
      const article: IArticle = { id: 30377 };
      const feed: IFeed = { id: 12331 };
      article.feed = feed;

      const feedCollection: IFeed[] = [{ id: 12331 }];
      jest.spyOn(feedService, 'query').mockReturnValue(of(new HttpResponse({ body: feedCollection })));
      const additionalFeeds = [feed];
      const expectedCollection: IFeed[] = [...additionalFeeds, ...feedCollection];
      jest.spyOn(feedService, 'addFeedToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ article });
      comp.ngOnInit();

      expect(feedService.query).toHaveBeenCalled();
      expect(feedService.addFeedToCollectionIfMissing).toHaveBeenCalledWith(
        feedCollection,
        ...additionalFeeds.map(expect.objectContaining),
      );
      expect(comp.feedsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const article: IArticle = { id: 30377 };
      const feed: IFeed = { id: 12331 };
      article.feed = feed;

      activatedRoute.data = of({ article });
      comp.ngOnInit();

      expect(comp.feedsSharedCollection).toContainEqual(feed);
      expect(comp.article).toEqual(article);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IArticle>>();
      const article = { id: 24128 };
      jest.spyOn(articleFormService, 'getArticle').mockReturnValue(article);
      jest.spyOn(articleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ article });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: article }));
      saveSubject.complete();

      // THEN
      expect(articleFormService.getArticle).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(articleService.update).toHaveBeenCalledWith(expect.objectContaining(article));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IArticle>>();
      const article = { id: 24128 };
      jest.spyOn(articleFormService, 'getArticle').mockReturnValue({ id: null });
      jest.spyOn(articleService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ article: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: article }));
      saveSubject.complete();

      // THEN
      expect(articleFormService.getArticle).toHaveBeenCalled();
      expect(articleService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IArticle>>();
      const article = { id: 24128 };
      jest.spyOn(articleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ article });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(articleService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareFeed', () => {
      it('Should forward to feedService', () => {
        const entity = { id: 12331 };
        const entity2 = { id: 6393 };
        jest.spyOn(feedService, 'compareFeed');
        comp.compareFeed(entity, entity2);
        expect(feedService.compareFeed).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
