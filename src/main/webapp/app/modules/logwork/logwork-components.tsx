import React, {useState, useEffect} from 'react';
import axios from 'axios';
import {durationToHours} from 'app/shared/util/date-utils';
import {TimeEntryToDuration} from "app/entities/time-entry/time-to-total.tsx";
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

  const [defaultProject, setDefaultProjects] = useState(null);

  const onChange = event => {
    const te = props.projects.find(p => p.id.toString() === event.target.value)
    props.handler(te);
  }

  useEffect(() => {
    if (props?.value) {
      setDefaultProjects(props.value)
    }
  })

  return (
    <FormGroup className="col-md-6">
      <Label for="project">Проект</Label>
      <Input type="select" name="project" id="project" onChange={onChange}>
        {props.projects.map((project, i) => (
          project === defaultProject ?
            <option key={i} value={project.id} selected>{project.name}</option> :
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

  function setTotal() {
    let sum = 0;
    props.entries.forEach(e => {
      sum = sum + durationToHours(e.duration);
    });
    setTotalHours(sum);
  }

  useEffect(() => {
    if (!props.entries || props.entries.length < 1) {
      return;
    }
    setTotal()
  })

  function saveEntity(event, errors, values, num) {
    const result = props.entries[num]
    result.duration = 'PT' + values.duration.toUpperCase();
    result.description = values.description
    result.shortDescription = values.shortDescription
    result.date = values.date
    axios.put('api/time-entries/', result)
    props.entries[num] = result
    setTotal();
    props.onUpdate ? props.onUpdate() : '';
  }


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
            <td>
              <TimeEntryToDuration entities={[entry]}/>
            </td>
            <td>{entry.shortDescription}</td>
            <td>{entry.date}</td>
            <td>
              <div>
                <TimeEntryUpdateModal entity={entry} saveEntity={saveEntity} num={i}/>
              </div>
            </td>
          </tr>
        ))
      ) : (
        <p>No Tasks</p>
      )}
      </tbody>
      <tr>
        <h5><TimeEntryToDuration entities={props.entries} added='Всего : '/></h5>
      </tr>
    </Table>
  );
}

