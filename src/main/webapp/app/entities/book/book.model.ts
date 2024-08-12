import { BookType } from 'app/entities/enumerations/book-type.model';
import { Genre } from 'app/entities/enumerations/genre.model';

export interface IBook {
  id: number;
  title?: string | null;
  type?: keyof typeof BookType | null;
  genre?: keyof typeof Genre | null;
}

export type NewBook = Omit<IBook, 'id'> & { id: null };
