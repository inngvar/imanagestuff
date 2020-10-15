import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import TaskConfig from './task-config';
import TaskConfigDetail from './task-config-detail';
import TaskConfigUpdate from './task-config-update';
import TaskConfigDeleteDialog from './task-config-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={TaskConfigUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={TaskConfigUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={TaskConfigDetail} />
      <ErrorBoundaryRoute path={match.url} component={TaskConfig} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={TaskConfigDeleteDialog} />
  </>
);

export default Routes;
