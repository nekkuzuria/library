import dayjs from 'dayjs/esm';
import { ILibrary } from 'app/entities/library/library.model';
import { ILocation } from 'app/entities/location/location.model';
import { IUser } from 'app/entities/user/user.model';

export interface ILibrarian {
  id: number;
  name?: string | null;
  email?: string | null;
  phoneNumber?: string | null;
  dateOfBirth?: dayjs.Dayjs | null;
  library?: Pick<ILibrary, 'id'> | null;
  location?: Pick<ILocation, 'id'> | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewLibrarian = Omit<ILibrarian, 'id'> & { id: null };
