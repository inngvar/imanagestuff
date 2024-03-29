import { IProject } from './project.model';

export interface IRegisteredTime {
  dayRegisteredTimes: Array<IDayRegisteredTime>;
}

export interface IDayRegisteredTime {
  date: Date;
  totalDuration: number;
  projectDurations?: Array<IProjectDuration>;
  unregisteredDuration: number;
  holiday: boolean;
}

export interface IProjectDuration {
  project: IProject;
  duration: number;
}
