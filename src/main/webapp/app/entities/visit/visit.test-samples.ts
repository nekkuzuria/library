import dayjs from 'dayjs/esm';

import { IVisit, NewVisit } from './visit.model';

export const sampleWithRequiredData: IVisit = {
  id: 27728,
};

export const sampleWithPartialData: IVisit = {
  id: 5518,
  date: dayjs('2024-08-12'),
};

export const sampleWithFullData: IVisit = {
  id: 24442,
  date: dayjs('2024-08-12'),
};

export const sampleWithNewData: NewVisit = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
