import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import TimeLog from './time-log';
import TimeLogDetail from './time-log-detail';
import TimeLogUpdate from './time-log-update';
import TimeLogDeleteDialog from './time-log-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={TimeLogUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={TimeLogUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={TimeLogDetail} />
      <ErrorBoundaryRoute path={match.url} component={TimeLog} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={TimeLogDeleteDialog} />
  </>
);

export default Routes;
