import { Moment } from 'moment';

export interface ITimeLog {
  id?: number;
  date?: string;
  checkIn?: string;
  checkOut?: string;
  memberLastName?: string;
  memberId?: number;
}

export const defaultValue: Readonly<ITimeLog> = {};
