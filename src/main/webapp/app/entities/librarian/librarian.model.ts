import dayjs from 'dayjs/esm';
import { ILibrary } from 'app/entities/library/library.model';
import { ILocation } from 'app/entities/location/location.model';

export interface ILibrarian {
  id: number;
  name?: string | null;
  email?: string | null;
  phoneNumber?: string | null;
  dateOfBirth?: dayjs.Dayjs | null;
  library?: Pick<ILibrary, 'id'> | null;
  location?: Pick<ILocation, 'id'> | null;
}

export type NewLibrarian = Omit<ILibrarian, 'id'> & { id: null };
