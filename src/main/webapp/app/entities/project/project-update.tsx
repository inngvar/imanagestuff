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
import { getEntity, updateEntity, createEntity, reset } from './project.reducer';
import { IProject } from 'app/shared/model/project.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IProjectUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ProjectUpdate = (props: IProjectUpdateProps) => {
  const [idsmembers, setIdsmembers] = useState([]);
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const { projectEntity, members, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/project' + props.location.search);
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
    if (errors.length === 0) {
      const entity = {
        ...projectEntity,
        ...values,
        members: mapIdList(values.members),
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
          <h2 id="imanagestuffApp.project.home.createOrEditLabel">
            <Translate contentKey="imanagestuffApp.project.home.createOrEditLabel">Create or edit a Project</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : projectEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="project-id">
                    <Translate contentKey="global.field.id">ID</Translate>
                  </Label>
                  <AvInput id="project-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="nameLabel" for="project-name">
                  <Translate contentKey="imanagestuffApp.project.name">Name</Translate>
                </Label>
                <AvField
                  id="project-name"
                  type="text"
                  name="name"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="descriptionLabel" for="project-description">
                  <Translate contentKey="imanagestuffApp.project.description">Description</Translate>
                </Label>
                <AvField
                  id="project-description"
                  type="text"
                  name="description"
                  validate={{
                    maxLength: { value: 4000, errorMessage: translate('entity.validation.maxlength', { max: 4000 }) },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="sendReportsLabel" for="project-sendReports">
                  <Translate contentKey="imanagestuffApp.project.sendReports">Send Reports</Translate>
                </Label>
                <AvField
                  id="project-sendReports"
                  type="text"
                  name="sendReports"
                  validate={{
                    maxLength: { value: 4000, errorMessage: translate('entity.validation.maxlength', { max: 4000 }) },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label for="project-members">
                  <Translate contentKey="imanagestuffApp.project.members">Members</Translate>
                </Label>
                <AvInput
                  id="project-members"
                  type="select"
                  multiple
                  className="form-control"
                  name="members"
                  value={projectEntity.members && projectEntity.members.map(e => e.id)}
                >
                  <option value="" key="0" />
                  {members
                    ? members.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.login}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/project" replace color="info">
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
  projectEntity: storeState.project.entity,
  loading: storeState.project.loading,
  updating: storeState.project.updating,
  updateSuccess: storeState.project.updateSuccess,
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

export default connect(mapStateToProps, mapDispatchToProps)(ProjectUpdate);
