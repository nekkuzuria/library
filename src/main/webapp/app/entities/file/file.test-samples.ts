import { IFile, NewFile } from './file.model';

export const sampleWithRequiredData: IFile = {
  id: 29851,
};

export const sampleWithPartialData: IFile = {
  id: 27581,
};

export const sampleWithFullData: IFile = {
  id: 26957,
  image: 'before fooey yesterday',
};

export const sampleWithNewData: NewFile = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
