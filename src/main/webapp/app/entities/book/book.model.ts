import { IBookStorage } from 'app/entities/book-storage/book-storage.model';
import { BookType } from 'app/entities/enumerations/book-type.model';
import { Genre } from 'app/entities/enumerations/genre.model';

export interface IBook {
  id: number;
  title?: string | null;
  type?: keyof typeof BookType | null;
  genre?: keyof typeof Genre | null;
  bookStorage?: Pick<IBookStorage, 'id'> | null;
}

export type NewBook = Omit<IBook, 'id'> & { id: null };
