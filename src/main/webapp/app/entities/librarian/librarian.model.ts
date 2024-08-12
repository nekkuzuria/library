import dayjs from 'dayjs/esm';

export interface ILibrarian {
  id: number;
  name?: string | null;
  email?: string | null;
  phoneNumber?: string | null;
  dateOfBirth?: dayjs.Dayjs | null;
}

export type NewLibrarian = Omit<ILibrarian, 'id'> & { id: null };
