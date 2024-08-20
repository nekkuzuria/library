import dayjs from 'dayjs/esm';

export interface IPersonalStorage {
  id: number;
  bookId?: number | null;
  title?: string | null;
  type?: string | null;
  genre?: string | null;
  year?: number | null;
  totalPage?: number | null;
  author?: string | null;
  cover?: string | null;
  borrowDate?: dayjs.Dayjs | null;
  returnDate?: dayjs.Dayjs | null;
  quantity: number;
}
