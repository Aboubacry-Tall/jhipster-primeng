export interface IFolder {
  id: number;
  name?: string | null;
  order?: number | null;
}

export type NewFolder = Omit<IFolder, 'id'> & { id: null };
