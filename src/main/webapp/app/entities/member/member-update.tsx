import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { ITaskConfig } from 'app/shared/model/task-config.model';
import { getEntities as getTaskConfigs } from 'app/entities/task-config/task-config.reducer';
import { IProject } from 'app/shared/model/project.model';
import { getEntities as getProjects } from 'app/entities/project/project.reducer';
import { getEntity, updateEntity, createEntity, reset } from './member.reducer';
import { IMember } from 'app/shared/model/member.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IMemberUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const MemberUpdate = (props: IMemberUpdateProps) => {
  const [taskConfigId, setTaskConfigId] = useState('0');
  const [projectsId, setProjectsId] = useState('0');
  const [isNew, setIsNew] = useState(!props.match.params || !props.match.params.id);

  const { memberEntity, taskConfigs, projects, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/member' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getTaskConfigs();
    props.getProjects();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...memberEntity,
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
          <h2 id="imanagestuffApp.member.home.createOrEditLabel">
            <Translate contentKey="imanagestuffApp.member.home.createOrEditLabel">Create or edit a Member</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : memberEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="member-id">
                    <Translate contentKey="global.field.id">ID</Translate>
                  </Label>
                  <AvInput id="member-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="loginLabel" for="member-login">
                  <Translate contentKey="imanagestuffApp.member.login">Login</Translate>
                </Label>
                <AvField
                  id="member-login"
                  type="text"
                  name="login"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="firstNameLabel" for="member-firstName">
                  <Translate contentKey="imanagestuffApp.member.firstName">First Name</Translate>
                </Label>
                <AvField
                  id="member-firstName"
                  type="text"
                  name="firstName"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="middleNameLabel" for="member-middleName">
                  <Translate contentKey="imanagestuffApp.member.middleName">Middle Name</Translate>
                </Label>
                <AvField id="member-middleName" type="text" name="middleName" />
              </AvGroup>
              <AvGroup>
                <Label id="lastNameLabel" for="member-lastName">
                  <Translate contentKey="imanagestuffApp.member.lastName">Last Name</Translate>
                </Label>
                <AvField
                  id="member-lastName"
                  type="text"
                  name="lastName"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label for="member-taskConfig">
                  <Translate contentKey="imanagestuffApp.member.taskConfig">Task Config</Translate>
                </Label>
                <AvInput id="member-taskConfig" type="select" className="form-control" name="taskConfigId">
                  <option value="" key="0" />
                  {taskConfigs
                    ? taskConfigs.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/member" replace color="info">
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
  taskConfigs: storeState.taskConfig.entities,
  projects: storeState.project.entities,
  memberEntity: storeState.member.entity,
  loading: storeState.member.loading,
  updating: storeState.member.updating,
  updateSuccess: storeState.member.updateSuccess,
});

const mapDispatchToProps = {
  getTaskConfigs,
  getProjects,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(MemberUpdate);
