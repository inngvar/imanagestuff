import { ITimeLog } from 'app/shared/model/time-log.model';
import { IProject } from 'app/shared/model/project.model';

export interface IMember {
  id?: number;
  login?: string;
  firstName?: string;
  middleName?: string;
  lastName?: string;
  defaultProjectName?: string;
  defaultProjectId?: number;
  timeLogs?: ITimeLog[];
  projects?: IProject[];
}

export const defaultValue: Readonly<IMember> = {};
