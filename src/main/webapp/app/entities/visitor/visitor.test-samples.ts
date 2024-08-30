import dayjs from 'dayjs/esm';

import { IVisitor, NewVisitor } from './visitor.model';

export const sampleWithRequiredData: IVisitor = {
  id: 27196,
};

export const sampleWithPartialData: IVisitor = {
  id: 16081,
  phoneNumber: 'handful arid',
  dateOfBirth: dayjs('2024-08-11'),
};

export const sampleWithFullData: IVisitor = {
  id: 18640,
  name: 'front',
  email: 'Winnifred_Witting35@gmail.com',
  phoneNumber: 'including',
  dateOfBirth: dayjs('2024-08-11'),
  membershipStatus: false,
};

export const sampleWithNewData: NewVisitor = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
