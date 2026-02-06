import React from 'react';
import PropTypes from 'prop-types';
import { FormGroup, Input, Label } from 'reactstrap';

export const MemberList = props => {
  const onChange = event => {
    const selectedMember = props.project.members.find(m => m.login === event.target.value);
    props.handler(selectedMember);
  };

  return (
    <FormGroup className="col-sm">
      <Label for="member">Участник</Label>
      {props.value ? (
        <Input type="select" name="member" id="member" value={props.value.login} onChange={onChange}>
          {props.project?.members.map((member, i) => (
            <option key={i} value={member.login}>
              {member?.firstName + ' ' + member?.lastName + '(' + member.login + ')'}
            </option>
          ))}
        </Input>
      ) : (
        <div>Empty</div>
      )}
    </FormGroup>
  );
};

MemberList.propTypes = {
  project: PropTypes.object,
  value: PropTypes.object,
  handler: PropTypes.func,
};
