import dayjs from 'dayjs/esm';

import { IVisitorBookStorage, NewVisitorBookStorage } from './visitor-book-storage.model';

export const sampleWithRequiredData: IVisitorBookStorage = {
  id: 7944,
};

export const sampleWithPartialData: IVisitorBookStorage = {
  id: 26187,
  borrowDate: dayjs('2024-08-12'),
  returnDate: dayjs('2024-08-12'),
};

export const sampleWithFullData: IVisitorBookStorage = {
  id: 27393,
  borrowDate: dayjs('2024-08-13'),
  returnDate: dayjs('2024-08-12'),
};

export const sampleWithNewData: NewVisitorBookStorage = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
