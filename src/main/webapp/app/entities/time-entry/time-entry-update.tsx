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
import { IProject } from 'app/shared/model/project.model';
import { getEntities as getProjects } from 'app/entities/project/project.reducer';
import { getEntity, updateEntity, createEntity, reset } from './time-entry.reducer';
import { ITimeEntry } from 'app/shared/model/time-entry.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface ITimeEntryUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const TimeEntryUpdate = (props: ITimeEntryUpdateProps) => {
  const [memberId, setMemberId] = useState('0');
  const [projectId, setProjectId] = useState('0');
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const { timeEntryEntity, members, projects, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/time-entry');
  };

  useEffect(() => {
    if (!isNew) {
      props.getEntity(props.match.params.id);
    }

    props.getMembers();
    props.getProjects();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    values.timestamp = convertDateTimeToServer(values.timestamp);

    if (errors.length === 0) {
      const entity = {
        ...timeEntryEntity,
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
          <h2 id="imanagestuffApp.timeEntry.home.createOrEditLabel">
            <Translate contentKey="imanagestuffApp.timeEntry.home.createOrEditLabel">Create or edit a TimeEntry</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : timeEntryEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="time-entry-id">
                    <Translate contentKey="global.field.id">ID</Translate>
                  </Label>
                  <AvInput id="time-entry-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="durationLabel" for="time-entry-duration">
                  <Translate contentKey="imanagestuffApp.timeEntry.duration">Duration</Translate>
                </Label>
                <AvField
                  id="time-entry-duration"
                  type="text"
                  name="duration"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="timestampLabel" for="time-entry-timestamp">
                  <Translate contentKey="imanagestuffApp.timeEntry.timestamp">Timestamp</Translate>
                </Label>
                <AvInput
                  id="time-entry-timestamp"
                  type="datetime-local"
                  className="form-control"
                  name="timestamp"
                  placeholder={'YYYY-MM-DD HH:mm'}
                  value={isNew ? displayDefaultDateTime() : convertDateTimeFromServer(props.timeEntryEntity.timestamp)}
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="shotDescriptionLabel" for="time-entry-shotDescription">
                  <Translate contentKey="imanagestuffApp.timeEntry.shotDescription">Shot Description</Translate>
                </Label>
                <AvField
                  id="time-entry-shotDescription"
                  type="text"
                  name="shotDescription"
                  validate={{
                    maxLength: { value: 256, errorMessage: translate('entity.validation.maxlength', { max: 256 }) },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="descriptionLabel" for="time-entry-description">
                  <Translate contentKey="imanagestuffApp.timeEntry.description">Description</Translate>
                </Label>
                <AvField
                  id="time-entry-description"
                  type="text"
                  name="description"
                  validate={{
                    maxLength: { value: 4000, errorMessage: translate('entity.validation.maxlength', { max: 4000 }) },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label for="time-entry-member">
                  <Translate contentKey="imanagestuffApp.timeEntry.member">Member</Translate>
                </Label>
                <AvInput
                  id="time-entry-member"
                  type="select"
                  className="form-control"
                  name="member.id"
                  value={isNew ? members[0] && members[0].id : timeEntryEntity.member?.id}
                  required
                >
                  {members
                    ? members.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.login}
                        </option>
                      ))
                    : null}
                </AvInput>
                <AvFeedback>
                  <Translate contentKey="entity.validation.required">This field is required.</Translate>
                </AvFeedback>
              </AvGroup>
              <AvGroup>
                <Label for="time-entry-project">
                  <Translate contentKey="imanagestuffApp.timeEntry.project">Project</Translate>
                </Label>
                <AvInput
                  id="time-entry-project"
                  type="select"
                  className="form-control"
                  name="project.id"
                  value={isNew ? projects[0] && projects[0].id : timeEntryEntity.project?.id}
                  required
                >
                  {projects
                    ? projects.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.name}
                        </option>
                      ))
                    : null}
                </AvInput>
                <AvFeedback>
                  <Translate contentKey="entity.validation.required">This field is required.</Translate>
                </AvFeedback>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/time-entry" replace color="info">
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
  projects: storeState.project.entities,
  timeEntryEntity: storeState.timeEntry.entity,
  loading: storeState.timeEntry.loading,
  updating: storeState.timeEntry.updating,
  updateSuccess: storeState.timeEntry.updateSuccess,
});

const mapDispatchToProps = {
  getMembers,
  getProjects,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(TimeEntryUpdate);
