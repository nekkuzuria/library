import dayjs from 'dayjs/esm';

import { ILibrary, NewLibrary } from './library.model';

export const sampleWithRequiredData: ILibrary = {
  id: 32082,
};

export const sampleWithPartialData: ILibrary = {
  id: 32497,
  name: 'er fluid',
  establishedDate: dayjs('2024-08-11'),
};

export const sampleWithFullData: ILibrary = {
  id: 23837,
  name: 'preside for',
  establishedDate: dayjs('2024-08-12'),
};

export const sampleWithNewData: NewLibrary = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
