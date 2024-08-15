import { IBook, NewBook } from './book.model';

export const sampleWithRequiredData: IBook = {
  id: 6816,
  title: 'hm offset brisk',
};

export const sampleWithPartialData: IBook = {
  id: 19314,
  title: 'aw',
  year: 32657,
  author: 'trim',
  cover: 'seldom',
};

export const sampleWithFullData: IBook = {
  id: 14559,
  title: 'burble',
  type: 'EBOOK',
  genre: 'ADVENTURE',
  year: 16278,
  totalPage: 23828,
  author: 'hm aside',
  cover: 'bake excluding glum',
  synopsis: 'uh-huh quest yowza',
};

export const sampleWithNewData: NewBook = {
  title: 'quizzically content yahoo',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
