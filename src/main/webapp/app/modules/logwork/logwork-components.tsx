import React, {useState, useEffect} from 'react';
import {Translate} from 'react-jhipster';
import {durationToHours} from 'app/shared/util/date-utils';
import { Link, RouteComponentProps } from 'react-router-dom';
import {
  Table,
  FormGroup,
  Input,
  Label,
  Button,
  FontAwesomeIcon
} from 'reactstrap';
import TimeEntryUpdateModal from "app/entities/time-entry/time-entry-modal";



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
  const [totalHours, setTotalHours] = useState(0);
  useEffect(() => {
    if (!props.entries || props.entries.length < 1) {
      return;
    }
    let sum = 0;
    props.entries.forEach(e => {
      sum = sum + durationToHours(e.duration);
    });
    setTotalHours(sum);
  })

  return (
      <Table className="table-striped table-hover table-sm">
        <thead className="thead-dark">
        <tr>
          <th scope="col">Часы</th>
          <th scope="col">Описание</th>
          <th scope="col">Дата</th>
          <th scope="col"/>
        </tr>
        </thead>
        <tbody>
        {props.entries ? (
          props.entries.map((entry, i) => (
            <tr key={i}>
              <td>{durationToHours(entry.duration)}</td>
              <td>{entry.shortDescription}</td>
              <td>{entry.date}</td>
              <td>
                <div >
                  <TimeEntryUpdateModal entity={entry} />
                </div>
              </td>
            </tr>
          ))
        ) : (
          <p>No Tasks</p>
        )}
        </tbody>
        <h5>Всего часов: {totalHours}</h5>
      </Table>
  );
}

