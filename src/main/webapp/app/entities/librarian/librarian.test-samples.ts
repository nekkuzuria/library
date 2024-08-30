import dayjs from 'dayjs/esm';

import { ILibrarian, NewLibrarian } from './librarian.model';

export const sampleWithRequiredData: ILibrarian = {
  id: 6277,
};

export const sampleWithPartialData: ILibrarian = {
  id: 25519,
  email: 'Carlos87@yahoo.com',
};

export const sampleWithFullData: ILibrarian = {
  id: 7859,
  name: 'ick',
  email: 'Olen.Keebler@yahoo.com',
  phoneNumber: 'what',
  dateOfBirth: dayjs('2024-08-11'),
};

export const sampleWithNewData: NewLibrarian = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
