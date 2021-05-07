import React, {useState, useEffect} from 'react';
import {
  Button,
  Row,
  Col,
  Form,
  FormGroup,
  Label,
  Modal, ModalHeader, ModalBody
} from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField, AvCheckboxGroup, AvCheckbox } from 'availity-reactstrap-validation';
import {Translate, translate} from 'react-jhipster';
import PropTypes from 'prop-types';

export const LogTimeModal = props => {

  return (
    <Modal key="logModal" isOpen={props.isShow} toggle={props.toggle}>
      <ModalHeader toggle={props.toggle}>Введите изменения</ModalHeader>
      <ModalBody>
        <AvForm onSubmit={props.onSave}>
          <AvGroup>
            <Label id="memberLabel" for="time-log-member">
              <Translate contentKey="imanagestuffApp.timeLog.member">Member</Translate>
            </Label>
            <AvField
              id="time-log-member"
              type="select"
              className="form-control"
              name="member"
              validate={{
                required: { value: true, errorMessage: translate('entity.validation.required') },
              }}
            />
          </AvGroup>
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
            <Label id="checkInLabel" for="time-log-checkin">
              <Translate contentKey="imanagestuffApp.timeLog.checkIn">Check in</Translate>
            </Label>
            <AvField
              id="time-log-checkin"
              type="time"
              className="form-control"
              name="checkin"
              validate={{
                required: { value: true, errorMessage: translate('entity.validation.required') },
              }}
            />
          </AvGroup>
          <FormGroup>
            <Button onClick={props.toggle} id="cancel-save" to="/time-entry" replace color="link">
              <Translate contentKey="entity.action.back">Back</Translate>
            </Button>
            &nbsp;&nbsp;
            <Button color="success" id="save-entity" type="submit" >
              <Translate contentKey="entity.action.save">Save</Translate>
            </Button>
          </FormGroup>
        </AvForm>
      </ModalBody>
    </Modal>
  )
}

LogTimeModal.propTypes = {
  isShow: PropTypes.bool,
  toggle: PropTypes.func,
  onSave: PropTypes.func
}
