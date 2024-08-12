import dayjs from 'dayjs/esm';

export interface IVisitor {
  id: number;
  name?: string | null;
  email?: string | null;
  phoneNumber?: string | null;
  dateOfBirth?: dayjs.Dayjs | null;
  membershipStatus?: boolean | null;
}

export type NewVisitor = Omit<IVisitor, 'id'> & { id: null };
