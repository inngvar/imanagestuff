import React, {useState, useEffect} from 'react';
import {Translate} from 'react-jhipster';

import {parse, end, toSeconds, pattern} from 'iso8601-duration';
import {
  Table,
  Dropdown,
  DropdownMenu,
  DropdownItem,
  DropdownToggle,
  Row,
  Form,
  FormGroup,
  Input,
  Label,
  Conqtainer
} from 'reactstrap';

function roundToTwo(num) {
  return Math.round((num + Number.EPSILON) * 100) / 100;
}


export const ProjectList = props => {


  const onChange = event => {
    const te = props.projects.find(p => p.id.toString() === event.target.value)
    props.handler(te);
  }

  return (
    <FormGroup className="col-md-6">
      <Label for="project">Проект</Label>
      <Input type="select" name="project" id="project" onChange={onChange}>
        {props.projects.map((project, i) => (
          <option key={i} value={project.id}>{project.name}</option>
        ))}
      </Input>
    </FormGroup>
  );
}

export const MemberList = props => {

  const onChange = event => {
    const selectedMember = props.project.members.find(m => m.login === event.target.value);
    props.handler(selectedMember);
  }

  return (
    <FormGroup className="col-md-6">
      <Label for="member">Участник</Label>
      {props.value ? (
          <Input type="select" name="member" id="member" value={props.value.login} onChange={onChange}>
            {props.project?.members.map((member, i) => (
              <option key={i}
                      value={member.login}>{member?.firstName + ' ' + member?.lastName + '(' + member.login + ')'}</option>
            ))}
          </Input>
        ) :
        (
          <div>Empty</div>
        )}
    </FormGroup>
  );
}

export const TimeEntries = props => {
  return (
      <Table className="table-striped table-hover table-sm">
        <thead className="thead-dark">
        <tr>
          <th scope="col">Часы</th>
          <th scope="col">Описание</th>
          <th scope="col">Дата</th>
        </tr>
        </thead>
        <tbody>
        {props.entries ? (
          props.entries.map((entry, i) => (
            <tr key={i}>
              <td>{roundToTwo(toSeconds(parse(entry.duration)) / 60 / 60)}</td>
              <td>{entry.shotDescription}</td>
              <td>{entry.date}</td>
            </tr>
          ))
        ) : (
          <p>No Tasks</p>
        )}
        </tbody>
      </Table>
  );
}

