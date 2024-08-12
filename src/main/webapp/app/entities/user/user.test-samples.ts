import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 25232,
  login: '8RGX',
};

export const sampleWithPartialData: IUser = {
  id: 13683,
  login: "oR@O\\FaG\\rA\\'u\\kZBUq\\g5",
};

export const sampleWithFullData: IUser = {
  id: 2560,
  login: '.dq8',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
