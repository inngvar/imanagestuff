import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import TimeCheckTask from './time-check-task';
import TimeCheckTaskDetail from './time-check-task-detail';
import TimeCheckTaskUpdate from './time-check-task-update';
import TimeCheckTaskDeleteDialog from './time-check-task-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={TimeCheckTaskUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={TimeCheckTaskUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={TimeCheckTaskDetail} />
      <ErrorBoundaryRoute path={match.url} component={TimeCheckTask} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={TimeCheckTaskDeleteDialog} />
  </>
);

export default Routes;
