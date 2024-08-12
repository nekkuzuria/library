import dayjs from 'dayjs/esm';

import { ILibrarian, NewLibrarian } from './librarian.model';

export const sampleWithRequiredData: ILibrarian = {
  id: 8528,
};

export const sampleWithPartialData: ILibrarian = {
  id: 15025,
  name: 'which',
  email: 'Gladyce_Kreiger44@gmail.com',
  phoneNumber: 'suddenly',
};

export const sampleWithFullData: ILibrarian = {
  id: 9782,
  name: 'terrible bird-watcher',
  email: 'Rosalyn.Leannon@gmail.com',
  phoneNumber: 'since between giddy',
  dateOfBirth: dayjs('2024-08-11'),
};

export const sampleWithNewData: NewLibrarian = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
