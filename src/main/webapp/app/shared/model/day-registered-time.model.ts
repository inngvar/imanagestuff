import { IProject } from './project.model';

export interface IRegisteredTime {
  dayRegisteredTimes: Array<IDayRegisteredTime>;
}

export interface IDayRegisteredTime {
  date?: Date;
  totalDuration?: string;
  projectDurations?: Array<IProjectDuration>;
}

export interface IProjectDuration {
  project?: IProject;
  duration?: string;
}
