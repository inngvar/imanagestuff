import { IProject } from 'app/shared/model/project.model';

export interface IMember {
  id?: number;
  login?: string;
  firstName?: string;
  middleName?: string;
  lastName?: string;
  projects?: IProject[];
}

export const defaultValue: Readonly<IMember> = {};
