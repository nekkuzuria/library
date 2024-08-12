import dayjs from 'dayjs/esm';

import { IVisitor, NewVisitor } from './visitor.model';

export const sampleWithRequiredData: IVisitor = {
  id: 26304,
};

export const sampleWithPartialData: IVisitor = {
  id: 13844,
  name: 'quickly',
  phoneNumber: 'bah if energetically',
  membershipStatus: true,
};

export const sampleWithFullData: IVisitor = {
  id: 6185,
  name: 'salvage',
  email: 'Lesley_Kovacek@gmail.com',
  phoneNumber: 'brr gifted lest',
  dateOfBirth: dayjs('2024-08-11'),
  membershipStatus: true,
};

export const sampleWithNewData: NewVisitor = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
