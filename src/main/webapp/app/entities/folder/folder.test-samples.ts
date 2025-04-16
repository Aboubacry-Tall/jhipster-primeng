import { IFolder, NewFolder } from './folder.model';

export const sampleWithRequiredData: IFolder = {
  id: 13315,
  name: 'frightfully closely',
};

export const sampleWithPartialData: IFolder = {
  id: 10072,
  name: 'mindless ape freely',
  order: 32059,
};

export const sampleWithFullData: IFolder = {
  id: 12076,
  name: 'willfully finally',
  order: 29951,
};

export const sampleWithNewData: NewFolder = {
  name: 'where recklessly',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
