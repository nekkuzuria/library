export interface IFile {
  id: number;
  image?: string | null;
}

export type NewFile = Omit<IFile, 'id'> & { id: null };
