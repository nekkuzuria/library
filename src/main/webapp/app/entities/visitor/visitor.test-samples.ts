import dayjs from 'dayjs/esm';

import { IVisitor, NewVisitor } from './visitor.model';

export const sampleWithRequiredData: IVisitor = {
  id: 24383,
};

export const sampleWithPartialData: IVisitor = {
  id: 14661,
  email: 'Ahmed11@gmail.com',
  phoneNumber: 'thankfully gosh furthermore',
};

export const sampleWithFullData: IVisitor = {
  id: 10304,
  name: 'boo favor',
  email: 'Brown14@gmail.com',
  phoneNumber: 'famously',
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
