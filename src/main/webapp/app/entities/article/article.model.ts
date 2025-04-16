import dayjs from 'dayjs/esm';
import { IFeed } from 'app/entities/feed/feed.model';

export interface IArticle {
  id: number;
  title?: string | null;
  link?: string | null;
  content?: string | null;
  summary?: string | null;
  author?: string | null;
  publishedDate?: dayjs.Dayjs | null;
  read?: boolean | null;
  favorited?: boolean | null;
  tags?: string | null;
  coverImageUrl?: string | null;
  views?: number | null;
  feed?: IFeed | null;
}

export type NewArticle = Omit<IArticle, 'id'> & { id: null };
