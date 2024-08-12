import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: 'df30e0d5-1182-4087-85e1-d75f6abd023b',
};

export const sampleWithPartialData: IAuthority = {
  name: 'e92062e6-12cb-4ebf-badf-fed7ed5ad612',
};

export const sampleWithFullData: IAuthority = {
  name: 'eae75305-7cc7-4ca7-a5be-807733aaa84a',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
