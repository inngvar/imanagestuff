import { Moment } from 'moment';
import { IMember } from 'app/shared/model/member.model';
import { IProject } from 'app/shared/model/project.model';

export interface ITimeEntry {
  id?: number;
  duration?: number;
  timestamp?: Moment;
  shotDescription?: string;
  descrption?: string;
  member?: IMember;
  project?: IProject;
}

export const defaultValue: Readonly<ITimeEntry> = {};
