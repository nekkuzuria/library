import { PendingTaskType } from './pendig-task-type.model';
import { PendingTaskStatus } from './pending-task-status.model';
import dayjs from 'dayjs/esm';

export interface IPendingTaskVM {
  id: number | null;
  type?: keyof typeof PendingTaskType | null;
  status?: keyof typeof PendingTaskStatus | null;
  visitorId: number;
  visitorName: string | null;
  bookId?: number | null;
  bookTitle?: string | null;
  bookAuthor?: string | null;
  librarianId: number | null;
  librarianName: string | null;
  quantity?: number | null;
  reason?: string | null;
  createdDate?: dayjs.Dayjs | null;
  visitorBookStorageId?: number | null;
}

export type NewPendingTask = Omit<
  IPendingTaskVM,
  | 'id'
  | 'status'
  | 'visitorId'
  | 'visitorName'
  | 'bookTitle'
  | 'bookAuthor'
  | 'librarianId'
  | 'librarianName'
  | 'reason'
  | 'createdDate'
  | 'visitorBookStorageId'
> & {
  id: null;
};

export type NewPendingTaskReturn = Omit<
  IPendingTaskVM,
  'id' | 'status' | 'visitorId' | 'visitorName' | 'bookTitle' | 'bookAuthor' | 'librarianId' | 'librarianName' | 'reason' | 'createdDate'
> & {
  id: null;
};
