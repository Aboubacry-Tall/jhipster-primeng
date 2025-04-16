import dayjs from 'dayjs/esm';

import { IFeed, NewFeed } from './feed.model';

export const sampleWithRequiredData: IFeed = {
  id: 18781,
  name: 'defenseless hence bowed',
  url: 'https://serpentine-topsail.org/',
};

export const sampleWithPartialData: IFeed = {
  id: 31613,
  name: 'after',
  url: 'https://negative-cow.info',
  siteName: 'snowplow',
  category: 'hospitalization wedding',
  autoDiscovered: true,
  createdDate: dayjs('2025-04-15T14:34'),
};

export const sampleWithFullData: IFeed = {
  id: 14363,
  name: 'confiscate mooch than',
  url: 'https://ajar-kielbasa.name',
  siteName: 'beneath oddly hmph',
  description: 'rarely fooey',
  category: 'ouch limply classic',
  autoDiscovered: false,
  faviconUrl: 'tightly around aw',
  active: true,
  createdDate: dayjs('2025-04-15T18:14'),
  lastFetchedDate: dayjs('2025-04-15T13:18'),
};

export const sampleWithNewData: NewFeed = {
  name: 'via while',
  url: 'https://low-newsstand.net/',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
