import { IBook, NewBook } from './book.model';

export const sampleWithRequiredData: IBook = {
  id: 11793,
  title: 'slim',
};

export const sampleWithPartialData: IBook = {
  id: 31840,
  title: 'questioningly woot',
  type: 'HARDCOVER',
};

export const sampleWithFullData: IBook = {
  id: 3479,
  title: 'truly',
  type: 'JOURNAL',
  genre: 'NONFICTION',
};

export const sampleWithNewData: NewBook = {
  title: 'provoke wrapping yum',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
