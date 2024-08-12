import dayjs from 'dayjs/esm';

import { ILibrary, NewLibrary } from './library.model';

export const sampleWithRequiredData: ILibrary = {
  id: 29431,
};

export const sampleWithPartialData: ILibrary = {
  id: 30713,
  name: 'upwardly',
};

export const sampleWithFullData: ILibrary = {
  id: 29470,
  name: 'pluralise',
  establishedDate: dayjs('2024-08-11'),
};

export const sampleWithNewData: NewLibrary = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
