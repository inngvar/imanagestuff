import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './time-check-task.reducer';
import { ITimeCheckTask } from 'app/shared/model/time-check-task.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ITimeCheckTaskDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const TimeCheckTaskDetail = (props: ITimeCheckTaskDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { timeCheckTaskEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          <Translate contentKey="imanagestuffApp.timeCheckTask.detail.title">TimeCheckTask</Translate> [<b>{timeCheckTaskEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="date">
              <Translate contentKey="imanagestuffApp.timeCheckTask.date">Date</Translate>
            </span>
          </dt>
          <dd>
            {timeCheckTaskEntity.date ? <TextFormat value={timeCheckTaskEntity.date} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
        </dl>
        <Button tag={Link} to="/time-check-task" replace color="info">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/time-check-task/${timeCheckTaskEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ timeCheckTask }: IRootState) => ({
  timeCheckTaskEntity: timeCheckTask.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(TimeCheckTaskDetail);
