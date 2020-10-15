import { IMember } from 'app/shared/model/member.model';

export interface ITaskConfig {
  id?: number;
  name?: string;
  members?: IMember[];
}

export const defaultValue: Readonly<ITaskConfig> = {};
