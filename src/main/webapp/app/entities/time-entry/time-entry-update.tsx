import React, {useEffect, useState} from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Button, Col, Label, Row} from 'reactstrap';
import {AvFeedback, AvField, AvForm, AvGroup, AvInput} from 'availity-reactstrap-validation';
import {Translate, translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {IRootState} from 'app/shared/reducers';
import {getEntities as getMembers} from 'app/entities/member/member.reducer';
import {getEntities as getProjects} from 'app/entities/project/project.reducer';
import {createEntity, getEntity, reset, updateEntity} from './time-entry.reducer';
import {
  convertToIsoDuration,
  formatDurationForDisplay,
  isValidDuration,
  minutesToDuration,
} from 'app/shared/util/date-utils';

export interface ITimeEntryUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const TimeEntryUpdate = (props: ITimeEntryUpdateProps) => {
  const [memberId, setMemberId] = useState('0');
  const [projectId, setProjectId] = useState('0');
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);
  const [durationError, setDurationError] = useState(null);

  const { timeEntryEntity, members, projects, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/time-entry' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
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
    if (errors.length === 0) {
      // Validate and convert duration
      if (!values.duration || !isValidDuration(values.duration)) {
        setDurationError(translate('imanagestuffApp.timeEntry.validation.invalidDuration'));
        return;
      }

      try {
        const isoDuration = convertToIsoDuration(values.duration);
        const entity = {
          ...timeEntryEntity,
          ...values,
          duration: isoDuration,
        };

        if (isNew) {
          props.createEntity(entity);
        } else {
          props.updateEntity(entity);
        }
      } catch (error) {
        setDurationError(error.message);
        return;
      }
    }
  };

  // Convert duration from ISO 8601 to user-friendly format for display
  const getInitialDuration = () => {
    if (!isNew && timeEntryEntity && timeEntryEntity.duration) {
      // If duration is already a number (minutes), convert to ISO 8601 first
      if (typeof timeEntryEntity.duration === 'number') {
        const isoDuration = minutesToDuration(timeEntryEntity.duration);
        return formatDurationForDisplay(isoDuration);
      }
      // If it's already a string in ISO 8601 format
      if (typeof timeEntryEntity.duration === 'string' && timeEntryEntity.duration.startsWith('PT')) {
        return formatDurationForDisplay(timeEntryEntity.duration);
      }
    }
    return '';
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
            <AvForm model={isNew ? { duration: '' } : { ...timeEntryEntity, duration: getInitialDuration() }} onSubmit={saveEntity}>
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
                  placeholder="e.g., 2h 30m, 2:30, 90m"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
                {durationError && <div className="invalid-feedback d-block">{durationError}</div>}
                <small className="form-text text-muted">
                  <Translate contentKey="imanagestuffApp.timeEntry.durationHelp">Use formats like: 2h 30m, 2:30, 90m, or 2h</Translate>
                </small>
              </AvGroup>
              <AvGroup>
                <Label id="dateLabel" for="time-entry-date">
                  <Translate contentKey="imanagestuffApp.timeEntry.date">Date</Translate>
                </Label>
                <AvField
                  id="time-entry-date"
                  type="date"
                  className="form-control"
                  name="date"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="shortDescriptionLabel" for="time-entry-shortDescription">
                  <Translate contentKey="imanagestuffApp.timeEntry.shortDescription">Short Description</Translate>
                </Label>
                <AvField
                  id="time-entry-shortDescription"
                  type="text"
                  name="shortDescription"
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
                <AvInput id="time-entry-member" type="select" className="form-control" name="memberId" required>
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
                <AvInput id="time-entry-project" type="select" className="form-control" name="projectId" required>
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
