import { ILocation, NewLocation } from './location.model';

export const sampleWithRequiredData: ILocation = {
  id: 12278,
  streetAddress: 'exactly enrich chow',
};

export const sampleWithPartialData: ILocation = {
  id: 16022,
  streetAddress: 'plight cheery',
  posttalCode: 'basil indent',
};

export const sampleWithFullData: ILocation = {
  id: 347,
  streetAddress: 'considering pail',
  posttalCode: 'gracefully urgently amid',
  city: 'Fort Miguelcester',
  stateProvince: 'truthfully',
};

export const sampleWithNewData: NewLocation = {
  streetAddress: 'dart form',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
