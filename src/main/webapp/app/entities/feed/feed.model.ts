import dayjs from 'dayjs/esm';

export interface IFeed {
  id: number;
  name?: string | null;
  url?: string | null;
  siteName?: string | null;
  description?: string | null;
  category?: string | null;
  autoDiscovered?: boolean | null;
  faviconUrl?: string | null;
  active?: boolean | null;
  createdDate?: dayjs.Dayjs | null;
  lastFetchedDate?: dayjs.Dayjs | null;
}

export type NewFeed = Omit<IFeed, 'id'> & { id: null };
