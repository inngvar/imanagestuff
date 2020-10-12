/* eslint react/no-multi-comp: 0, react/prop-types: 0 */
import axios from 'axios';
import React, { useState, useEffect } from 'react';
import { Button, Modal, ModalHeader, ModalBody, Label, FormGroup, Link  } from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { Translate, translate, } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import {durationToHours} from "app/shared/util/date-utils";


const TimeEntryUpdateModal = (props) => {
  const {
    className,
    num,
    entity
  } = props;

  const [modal, setModal] = useState(false);

  const toggle = () => {
    setModal(!modal)
  };

  function saveEntity (event, errors, values) {
    props.saveEntity(event, errors, values, num);
    toggle()
  }

  return (
    <div>
      <Button color="primary" onClick={toggle}>Изменить</Button>
      <Modal isOpen={modal} toggle={toggle} className={className}>
        <ModalHeader toggle={toggle}>Введите изменения</ModalHeader>
        <ModalBody>
          <AvForm model={entity} onSubmit={saveEntity}>
              <AvGroup>
                <Label for="time-entry-id">
                  <Translate contentKey="global.field.id">ID</Translate>
                </Label>
                <AvInput id="time-entry-id" type="text" className="form-control" name="id" required readOnly />
              </AvGroup>
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
                type="textarea"
                name="description"
                validate={{
                  maxLength: { value: 4000, errorMessage: translate('entity.validation.maxlength', { max: 4000 }) },
                }}
              />
            </AvGroup>
            <FormGroup>
              <Button onClick={toggle} id="cancel-save" to="/time-entry" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;&nbsp;
              <Button color="primary" id="save-entity" type="submit" >
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </FormGroup>
          </AvForm>
        </ModalBody>
      </Modal>
    </div>
  );
}

export default TimeEntryUpdateModal;
