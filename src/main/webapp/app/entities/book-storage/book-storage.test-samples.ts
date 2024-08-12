import { IBookStorage, NewBookStorage } from './book-storage.model';

export const sampleWithRequiredData: IBookStorage = {
  id: 1644,
};

export const sampleWithPartialData: IBookStorage = {
  id: 21585,
};

export const sampleWithFullData: IBookStorage = {
  id: 32710,
  quantity: 31990,
};

export const sampleWithNewData: NewBookStorage = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
