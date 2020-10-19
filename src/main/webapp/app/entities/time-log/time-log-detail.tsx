import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './time-log.reducer';
import { ITimeLog } from 'app/shared/model/time-log.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ITimeLogDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const TimeLogDetail = (props: ITimeLogDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { timeLogEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          <Translate contentKey="imanagestuffApp.timeLog.detail.title">TimeLog</Translate> [<b>{timeLogEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="date">
              <Translate contentKey="imanagestuffApp.timeLog.date">Date</Translate>
            </span>
          </dt>
          <dd>{timeLogEntity.date ? <TextFormat value={timeLogEntity.date} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="checkIn">
              <Translate contentKey="imanagestuffApp.timeLog.checkIn">Check In</Translate>
            </span>
          </dt>
          <dd>{timeLogEntity.checkIn ? <TextFormat value={timeLogEntity.checkIn} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="checkOut">
              <Translate contentKey="imanagestuffApp.timeLog.checkOut">Check Out</Translate>
            </span>
          </dt>
          <dd>{timeLogEntity.checkOut ? <TextFormat value={timeLogEntity.checkOut} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="imanagestuffApp.timeLog.member">Member</Translate>
          </dt>
          <dd>{timeLogEntity.memberLastName ? timeLogEntity.memberLastName : ''}</dd>
        </dl>
        <Button tag={Link} to="/time-log" replace color="info">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/time-log/${timeLogEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ timeLog }: IRootState) => ({
  timeLogEntity: timeLog.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(TimeLogDetail);
