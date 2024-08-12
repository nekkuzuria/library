export interface IBookStorage {
  id: number;
  quantity?: number | null;
}

export type NewBookStorage = Omit<IBookStorage, 'id'> & { id: null };
