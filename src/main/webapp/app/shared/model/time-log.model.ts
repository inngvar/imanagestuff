import { Moment } from 'moment';

export interface ITimeLog {
  id?: number;
  timestamp?: string;
  memberLogin?: string;
  memberId?: number;
  timeCheckTaskId?: number;
}

export const defaultValue: Readonly<ITimeLog> = {};
