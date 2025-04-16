import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../feed.test-samples';

import { FeedFormService } from './feed-form.service';

describe('Feed Form Service', () => {
  let service: FeedFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FeedFormService);
  });

  describe('Service methods', () => {
    describe('createFeedFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFeedFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            url: expect.any(Object),
            siteName: expect.any(Object),
            description: expect.any(Object),
            category: expect.any(Object),
            autoDiscovered: expect.any(Object),
            faviconUrl: expect.any(Object),
            active: expect.any(Object),
            createdDate: expect.any(Object),
            lastFetchedDate: expect.any(Object),
          }),
        );
      });

      it('passing IFeed should create a new form with FormGroup', () => {
        const formGroup = service.createFeedFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            url: expect.any(Object),
            siteName: expect.any(Object),
            description: expect.any(Object),
            category: expect.any(Object),
            autoDiscovered: expect.any(Object),
            faviconUrl: expect.any(Object),
            active: expect.any(Object),
            createdDate: expect.any(Object),
            lastFetchedDate: expect.any(Object),
          }),
        );
      });
    });

    describe('getFeed', () => {
      it('should return NewFeed for default Feed initial value', () => {
        const formGroup = service.createFeedFormGroup(sampleWithNewData);

        const feed = service.getFeed(formGroup) as any;

        expect(feed).toMatchObject(sampleWithNewData);
      });

      it('should return NewFeed for empty Feed initial value', () => {
        const formGroup = service.createFeedFormGroup();

        const feed = service.getFeed(formGroup) as any;

        expect(feed).toMatchObject({});
      });

      it('should return IFeed', () => {
        const formGroup = service.createFeedFormGroup(sampleWithRequiredData);

        const feed = service.getFeed(formGroup) as any;

        expect(feed).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFeed should not enable id FormControl', () => {
        const formGroup = service.createFeedFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFeed should disable id FormControl', () => {
        const formGroup = service.createFeedFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
