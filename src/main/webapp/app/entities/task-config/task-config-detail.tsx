import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './task-config.reducer';
import { ITaskConfig } from 'app/shared/model/task-config.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ITaskConfigDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const TaskConfigDetail = (props: ITaskConfigDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { taskConfigEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          <Translate contentKey="imanagestuffApp.taskConfig.detail.title">TaskConfig</Translate> [<b>{taskConfigEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="name">
              <Translate contentKey="imanagestuffApp.taskConfig.name">Name</Translate>
            </span>
          </dt>
          <dd>{taskConfigEntity.name}</dd>
        </dl>
        <Button tag={Link} to="/task-config" replace color="info">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/task-config/${taskConfigEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ taskConfig }: IRootState) => ({
  taskConfigEntity: taskConfig.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(TaskConfigDetail);
