import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Member from './member';
import MemberDetail from './member-detail';
import MemberUpdate from './member-update';
import MemberDeleteDialog from './member-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={MemberUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={MemberUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={MemberDetail} />
      <ErrorBoundaryRoute path={match.url} component={Member} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={MemberDeleteDialog} />
  </>
);

export default Routes;
