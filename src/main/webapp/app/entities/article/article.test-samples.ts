import dayjs from 'dayjs/esm';

import { IArticle, NewArticle } from './article.model';

export const sampleWithRequiredData: IArticle = {
  id: 17774,
  title: 'however draft weight',
  link: 'decriminalize pants ornate',
};

export const sampleWithPartialData: IArticle = {
  id: 13517,
  title: 'lest option',
  link: 'wallop often vivacious',
  summary: '../fake-data/blob/hipster.txt',
  author: 'lest pixellate',
  favorited: true,
  tags: 'alive compete',
};

export const sampleWithFullData: IArticle = {
  id: 28493,
  title: 'athwart',
  link: 'yearly nicely rise',
  content: '../fake-data/blob/hipster.txt',
  summary: '../fake-data/blob/hipster.txt',
  author: 'during season vice',
  publishedDate: dayjs('2025-04-16T09:03'),
  read: false,
  favorited: true,
  tags: 'ick stranger phooey',
  coverImageUrl: 'glow',
  views: 6549,
};

export const sampleWithNewData: NewArticle = {
  title: 'yum',
  link: 'rule',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
