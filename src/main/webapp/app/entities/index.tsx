import React from 'react';
import { Switch } from 'react-router-dom';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Project from './project';
import Member from './member';
import TimeEntry from './time-entry';
import TimeLog from './time-log';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}project`} component={Project} />
      <ErrorBoundaryRoute path={`${match.url}member`} component={Member} />
      <ErrorBoundaryRoute path={`${match.url}time-entry`} component={TimeEntry} />
      <ErrorBoundaryRoute path={`${match.url}time-log`} component={TimeLog} />
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
