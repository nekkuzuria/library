import dayjs from 'dayjs/esm';

export interface IVisit {
  id: number;
  date?: dayjs.Dayjs | null;
}

export type NewVisit = Omit<IVisit, 'id'> & { id: null };
