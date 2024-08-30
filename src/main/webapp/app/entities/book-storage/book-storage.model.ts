import { ILibrary } from 'app/entities/library/library.model';

export interface IBookStorage {
  id: number;
  quantity?: number | null;
  library?: Pick<ILibrary, 'id'> | null;
}

export type NewBookStorage = Omit<IBookStorage, 'id'> & { id: null };
