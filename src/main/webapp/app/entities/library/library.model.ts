import dayjs from 'dayjs/esm';

export interface ILibrary {
  id: number;
  name?: string | null;
  establishedDate?: dayjs.Dayjs | null;
}

export type NewLibrary = Omit<ILibrary, 'id'> & { id: null };
