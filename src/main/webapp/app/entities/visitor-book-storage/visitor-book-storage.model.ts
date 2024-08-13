import dayjs from 'dayjs/esm';
import { IVisitor } from 'app/entities/visitor/visitor.model';
import { IBook } from 'app/entities/book/book.model';

export interface IVisitorBookStorage {
  id: number;
  borrowDate?: dayjs.Dayjs | null;
  returnDate?: dayjs.Dayjs | null;
  visitor?: Pick<IVisitor, 'id'> | null;
  book?: Pick<IBook, 'id'> | null;
}

export type NewVisitorBookStorage = Omit<IVisitorBookStorage, 'id'> & { id: null };
