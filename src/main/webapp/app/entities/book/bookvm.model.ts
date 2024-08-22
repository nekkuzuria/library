import { BookType } from 'app/entities/enumerations/book-type.model';
import { Genre } from 'app/entities/enumerations/genre.model';

export interface IBookVM {
  id: number;
  title?: string | null;
  type?: keyof typeof BookType | null;
  genre?: keyof typeof Genre | null;
  year?: number | null;
  totalPage?: number | null;
  author?: string | null;
  cover?: string | null;
  synopsis?: string | null;
  bookStorageId?: number | null;
  quantity?: number | null;
}

export type NewBook = Omit<IBookVM, 'id'> & { id: null };
