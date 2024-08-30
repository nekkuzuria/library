import dayjs from 'dayjs/esm';
import { ILocation } from 'app/entities/location/location.model';
import { ILibrary } from 'app/entities/library/library.model';
import { IUser } from 'app/entities/user/user.model';

export interface IVisitor {
  id: number;
  name?: string | null;
  email?: string | null;
  phoneNumber?: string | null;
  dateOfBirth?: dayjs.Dayjs | null;
  membershipStatus?: boolean | null;
  address?: Pick<ILocation, 'id'> | null;
  library?: Pick<ILibrary, 'id'> | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewVisitor = Omit<IVisitor, 'id'> & { id: null };
