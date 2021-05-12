import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IMember } from 'app/shared/model/member.model';
import { getEntities as getMembers } from 'app/entities/member/member.reducer';
import { getEntity, updateEntity, createEntity, reset } from './time-log.reducer';
import { ITimeLog } from 'app/shared/model/time-log.model';
import {
  convertDateTimeFromServer,
  convertDateTimeToServer,
  displayDefaultDateTime,
  formatDateTimeWithZone
} from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface ITimeLogUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const TimeLogUpdate = (props: ITimeLogUpdateProps) => {
  const [memberId, setMemberId] = useState('0');
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const { timeLogEntity, members, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/time-log' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getMembers();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    values.checkIn = formatDateTimeWithZone(values.checkIn);
    values.checkOut = formatDateTimeWithZone(values.checkOut);

    if (errors.length === 0) {
      const entity = {
        ...timeLogEntity,
        ...values,
      };

      if (isNew) {
        props.createEntity(entity);
      } else {
        props.updateEntity(entity);
      }
    }
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="imanagestuffApp.timeLog.home.createOrEditLabel">
            <Translate contentKey="imanagestuffApp.timeLog.home.createOrEditLabel">Create or edit a TimeLog</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : timeLogEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="time-log-id">
                    <Translate contentKey="global.field.id">ID</Translate>
                  </Label>
                  <AvInput id="time-log-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="dateLabel" for="time-log-date">
                  <Translate contentKey="imanagestuffApp.timeLog.date">Date</Translate>
                </Label>
                <AvField
                  id="time-log-date"
                  type="date"
                  className="form-control"
                  name="date"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="checkInLabel" for="time-log-checkIn">
                  <Translate contentKey="imanagestuffApp.timeLog.checkIn">Check In</Translate>
                </Label>
                <AvInput
                  id="time-log-checkIn"
                  type="datetime-local"
                  className="form-control"
                  name="checkIn"
                  placeholder={'YYYY-MM-DD HH:mm'}
                  value={isNew ? displayDefaultDateTime() : convertDateTimeFromServer(props.timeLogEntity.checkIn)}
                />
              </AvGroup>
              <AvGroup>
                <Label id="checkOutLabel" for="time-log-checkOut">
                  <Translate contentKey="imanagestuffApp.timeLog.checkOut">Check Out</Translate>
                </Label>
                <AvInput
                  id="time-log-checkOut"
                  type="datetime-local"
                  className="form-control"
                  name="checkOut"
                  placeholder={'YYYY-MM-DD HH:mm'}
                  value={isNew ? displayDefaultDateTime() : convertDateTimeFromServer(props.timeLogEntity.checkOut)}
                />
              </AvGroup>
              <AvGroup>
                <Label for="time-log-member">
                  <Translate contentKey="imanagestuffApp.timeLog.member">Member</Translate>
                </Label>
                <AvInput id="time-log-member" type="select" className="form-control" name="memberId" required>
                  {members
                    ? members.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.lastName}
                        </option>
                      ))
                    : null}
                </AvInput>
                <AvFeedback>
                  <Translate contentKey="entity.validation.required">This field is required.</Translate>
                </AvFeedback>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/time-log" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </AvForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  members: storeState.member.entities,
  timeLogEntity: storeState.timeLog.entity,
  loading: storeState.timeLog.loading,
  updating: storeState.timeLog.updating,
  updateSuccess: storeState.timeLog.updateSuccess,
});

const mapDispatchToProps = {
  getMembers,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(TimeLogUpdate);
