import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './time-entry.reducer';
import { ITimeEntry } from 'app/shared/model/time-entry.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ITimeEntryDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const TimeEntryDetail = (props: ITimeEntryDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { timeEntryEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          <Translate contentKey="imanagestuffApp.timeEntry.detail.title">TimeEntry</Translate> [<b>{timeEntryEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="duration">
              <Translate contentKey="imanagestuffApp.timeEntry.duration">Duration</Translate>
            </span>
          </dt>
          <dd>{timeEntryEntity.duration}</dd>
          <dt>
            <span id="timestamp">
              <Translate contentKey="imanagestuffApp.timeEntry.timestamp">Timestamp</Translate>
            </span>
          </dt>
          <dd>
            {timeEntryEntity.timestamp ? <TextFormat value={timeEntryEntity.timestamp} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="shotDescription">
              <Translate contentKey="imanagestuffApp.timeEntry.shotDescription">Shot Description</Translate>
            </span>
          </dt>
          <dd>{timeEntryEntity.shotDescription}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="imanagestuffApp.timeEntry.description">Description</Translate>
            </span>
          </dt>
          <dd>{timeEntryEntity.description}</dd>
          <dt>
            <Translate contentKey="imanagestuffApp.timeEntry.member">Member</Translate>
          </dt>
          <dd>{timeEntryEntity.member ? timeEntryEntity.member.login : ''}</dd>
          <dt>
            <Translate contentKey="imanagestuffApp.timeEntry.project">Project</Translate>
          </dt>
          <dd>{timeEntryEntity.project ? timeEntryEntity.project.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/time-entry" replace color="info">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/time-entry/${timeEntryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ timeEntry }: IRootState) => ({
  timeEntryEntity: timeEntry.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(TimeEntryDetail);
