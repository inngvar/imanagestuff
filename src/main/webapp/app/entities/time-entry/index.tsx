import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import TimeEntry from './time-entry';
import TimeEntryDetail from './time-entry-detail';
import TimeEntryUpdate from './time-entry-update';
import TimeEntryDeleteDialog from './time-entry-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={TimeEntryDeleteDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={TimeEntryUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={TimeEntryUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={TimeEntryDetail} />
      <ErrorBoundaryRoute path={match.url} component={TimeEntry} />
    </Switch>
  </>
);

export default Routes;
