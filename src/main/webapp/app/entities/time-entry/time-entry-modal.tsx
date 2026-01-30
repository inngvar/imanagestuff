/* eslint react/no-multi-comp: 0, react/prop-types: 0 */

import React, { useState, useEffect } from 'react';
import { Button, Modal, ModalHeader, ModalBody, Label, FormGroup, Link } from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { convertToIsoDuration, isValidDuration, formatDurationForDisplay, minutesToDuration } from 'app/shared/util/date-utils';

const TimeEntryUpdateModal = props => {
  const { className, num, entity } = props;

  const [modal, setModal] = useState(false);
  const [member, setMember] = useState(null);
  const [currentMember, setCurrentMember] = useState(null);
  const [durationError, setDurationError] = useState(null);

  useEffect(() => {
    if (props.member !== null) {
      setMember(props.member);
    }
  });
  useEffect(() => {
    if (props.currentMember !== null) {
      setCurrentMember(props.currentMember);
    }
  });

  const toggle = () => {
    setModal(!modal);
    setDurationError(null);
  };

  // Convert duration from ISO 8601 to user-friendly format for display
  const getInitialDuration = () => {
    if (entity && entity.duration) {
      // If duration is already a number (minutes), convert to ISO 8601 first
      if (typeof entity.duration === 'number') {
        const isoDuration = minutesToDuration(entity.duration);
        return formatDurationForDisplay(isoDuration);
      }
      // If it's already a string in ISO 8601 format
      if (typeof entity.duration === 'string' && entity.duration.startsWith('PT')) {
        return formatDurationForDisplay(entity.duration);
      }
    }
    return '';
  };

  function saveEntity(event, errors, values) {
    // Validate duration input
    if (!values.duration || !isValidDuration(values.duration)) {
      setDurationError(translate('imanagestuffApp.timeEntry.validation.invalidDuration'));
      return;
    }

    try {
      const isoDuration = convertToIsoDuration(values.duration);
      const updatedValues = {
        ...values,
        duration: isoDuration,
      };
      props.saveEntity(event, errors, updatedValues, num);
      toggle();
    } catch (error) {
      setDurationError(error.message);
    }
  }

  return (
    <div>
      <Button color="primary" onClick={toggle} disabled={member?.login !== currentMember?.login}>
        Изменить
      </Button>
      <Modal isOpen={modal} toggle={toggle} className={className}>
        <ModalHeader toggle={toggle}>Введите изменения</ModalHeader>
        <ModalBody>
          <AvForm model={{ ...entity, duration: getInitialDuration() }} onSubmit={saveEntity}>
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
              <Button color="primary" id="save-entity" type="submit">
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
};

export default TimeEntryUpdateModal;
