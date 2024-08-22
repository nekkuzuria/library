import { BookType } from 'app/entities/enumerations/book-type.model';
import { Genre } from 'app/entities/enumerations/genre.model';
import { IFile } from 'app/entities/file/file.model';
import { IBookStorage } from 'app/entities/book-storage/book-storage.model';

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
  bookStorageId?: Pick<IBookStorage, 'id'> | null;
  quantity?: number | null;
  file?: Pick<IFile, 'id'> | null;
}

export type NewBook = Omit<IBookVM, 'id'> & { id: null };
