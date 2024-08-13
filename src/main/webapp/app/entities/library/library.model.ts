import dayjs from 'dayjs/esm';
import { ILocation } from 'app/entities/location/location.model';

export interface ILibrary {
  id: number;
  name?: string | null;
  establishedDate?: dayjs.Dayjs | null;
  location?: Pick<ILocation, 'id'> | null;
}

export type NewLibrary = Omit<ILibrary, 'id'> & { id: null };
