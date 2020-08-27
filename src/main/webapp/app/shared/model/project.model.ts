import { IMember } from 'app/shared/model/member.model';

export interface IProject {
  id?: number;
  name?: string;
  descrption?: string;
  sendReports?: string;
  members?: IMember[];
}

export const defaultValue: Readonly<IProject> = {};
