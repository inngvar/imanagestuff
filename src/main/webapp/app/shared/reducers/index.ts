import { combineReducers } from 'redux';
import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import locale, { LocaleState } from './locale';
import authentication, { AuthenticationState } from './authentication';
import applicationProfile, { ApplicationProfileState } from './application-profile';

import administration, { AdministrationState } from 'app/modules/administration/administration.reducer';
import userManagement, { UserManagementState } from 'app/modules/administration/user-management/user-management.reducer';
import register, { RegisterState } from 'app/modules/account/register/register.reducer';
import activate, { ActivateState } from 'app/modules/account/activate/activate.reducer';
import password, { PasswordState } from 'app/modules/account/password/password.reducer';
import settings, { SettingsState } from 'app/modules/account/settings/settings.reducer';
import passwordReset, { PasswordResetState } from 'app/modules/account/password-reset/password-reset.reducer';
// prettier-ignore
import project, {
  ProjectState
} from 'app/entities/project/project.reducer';
// prettier-ignore
import member, {
  MemberState
} from 'app/entities/member/member.reducer';
// prettier-ignore
import timeEntry, {
  TimeEntryState
} from 'app/entities/time-entry/time-entry.reducer';
// prettier-ignore
import timeLog, {
  TimeLogState
} from 'app/entities/time-log/time-log.reducer';
// prettier-ignore
import timeCheckTask, {
  TimeCheckTaskState
} from 'app/entities/time-check-task/time-check-task.reducer';
// prettier-ignore
import taskConfig, {
  TaskConfigState
} from 'app/entities/task-config/task-config.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

export interface IRootState {
  readonly authentication: AuthenticationState;
  readonly locale: LocaleState;
  readonly applicationProfile: ApplicationProfileState;
  readonly administration: AdministrationState;
  readonly userManagement: UserManagementState;
  readonly register: RegisterState;
  readonly activate: ActivateState;
  readonly passwordReset: PasswordResetState;
  readonly password: PasswordState;
  readonly settings: SettingsState;
  readonly project: ProjectState;
  readonly member: MemberState;
  readonly timeEntry: TimeEntryState;
  readonly timeLog: TimeLogState;
  readonly timeCheckTask: TimeCheckTaskState;
  readonly taskConfig: TaskConfigState;
  /* jhipster-needle-add-reducer-type - JHipster will add reducer type here */
  readonly loadingBar: any;
}

const rootReducer = combineReducers<IRootState>({
  authentication,
  locale,
  applicationProfile,
  administration,
  userManagement,
  register,
  activate,
  passwordReset,
  password,
  settings,
  project,
  member,
  timeEntry,
  timeLog,
  timeCheckTask,
  taskConfig,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
  loadingBar,
});

export default rootReducer;
