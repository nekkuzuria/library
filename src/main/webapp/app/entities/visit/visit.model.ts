import dayjs from 'dayjs/esm';
import { ILibrary } from 'app/entities/library/library.model';
import { ILibrarian } from 'app/entities/librarian/librarian.model';
import { IVisitor } from 'app/entities/visitor/visitor.model';

export interface IVisit {
  id: number;
  date?: dayjs.Dayjs | null;
  library?: Pick<ILibrary, 'id'> | null;
  librarian?: Pick<ILibrarian, 'id'> | null;
  visitor?: Pick<IVisitor, 'id'> | null;
}

export type NewVisit = Omit<IVisit, 'id'> & { id: null };
