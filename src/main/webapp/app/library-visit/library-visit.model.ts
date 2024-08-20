import dayjs from 'dayjs/esm';

export interface IVisitVM {
  id: number;
  bookId: number;
  title: string;
  type: string;
  author: string;
  cover: string;
  visitorBookStorageId: number;
  borrowDate: dayjs.Dayjs | null;
  returnDate: dayjs.Dayjs | null;
  librarianId: number;
  librarianName: string;
  visitorId: number;
  visitorName: string;
}
