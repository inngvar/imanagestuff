import { Moment } from 'moment';
import { IMember } from 'app/shared/model/member.model';
import { IProject } from 'app/shared/model/project.model';

export interface ITimeEntry {
  id?: number;
  duration?: number;
  timestamp?: string;
  shotDescription?: string;
  description?: string;
  member?: IMember;
  project?: IProject;
}

export const defaultValue: Readonly<ITimeEntry> = {};
