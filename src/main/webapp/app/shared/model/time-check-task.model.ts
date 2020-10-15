import { Moment } from 'moment';
import { ITimeLog } from 'app/shared/model/time-log.model';

export interface ITimeCheckTask {
  id?: number;
  date?: string;
  checks?: ITimeLog[];
}

export const defaultValue: Readonly<ITimeCheckTask> = {};
