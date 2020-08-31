import React, {useState, useEffect} from 'react';
import {Translate} from 'react-jhipster';

import {NavItem, NavLink, NavbarBrand} from 'reactstrap';
import {NavLink as Link} from 'react-router-dom';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {
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

export const ProjectList = props => {


  const onChange = event => {
    const te = props.projects.find(p => p.id.toString() === event.target.value)
    props.handler(te);
  }

  return (
    <FormGroup>
      <Label>Проект</Label>
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
    <FormGroup>
      <Label>Участник {props.value.login}</Label>
      {props.member ? (
          <div>Empty</div>
        ) :
        (
          <Input type="select" name="member" id="member" value={props.value.login} onChange={onChange}>
            {props.project?.members.map((member, i) => (
              <option key={i}
                      value={member.login}>{member?.firstName + ' ' + member?.lastName + '(' + member.login + ')'}</option>
            ))}
          </Input>

        )}


      {/*    <Input type="select" name="member" id="member" value={props.member?.login}>
        {props.project?.members.map((member, i) => (
          <option key={i}
                  value={member.id}>{member?.firstName + ' ' + member?.lastName + '(' + member.login + ')'}</option>
        ))}
      </Input>*/}
    </FormGroup>

  );
}
