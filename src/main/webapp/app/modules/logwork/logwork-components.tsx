import React, {useState, useEffect} from 'react';
import axios from 'axios';
import {parseTime} from 'app/shared/util/date-utils';
import {TimeEntryToDuration} from "app/entities/time-entry/time-to-total.tsx";
import {
  Table,
  FormGroup,
  Input,
  Label,
  InputGroup,
  InputGroupAddon,
  Button
} from 'reactstrap';
import TimeEntryUpdateModal from "app/entities/time-entry/time-entry-modal";
import PropTypes from 'prop-types';

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
    <div className="col-md-6">
      <Label for="project">Проект</Label>
      <InputGroup >
        { props?.showButton ? <InputGroupAddon addonType="prepend">
          <Button color="info"
                  disabled={props.isDefaultProject}
                  onClick={props.updateDefaultProject}>
            По умолчанию
          </Button>
        </InputGroupAddon> : ''}
        <Input type="select" name="project" id="project" onChange={onChange}>
        {props.projects.map((project, i) => (
          project === defaultProject ?
            <option key={i} value={project.id} selected>{project.name}</option> :
            <option key={i} value={project.id}>{project.name}</option>
        ))}
      </Input>
      </InputGroup>
    </div>
  );
}

ProjectList.propTypes = {
  isDefaultProject: PropTypes.bool,
  updateDefaultProject: PropTypes.func,
  projects: PropTypes.arrayOf(PropTypes.object),
  value: PropTypes.object,
  handler: PropTypes.func,
  showButton: PropTypes.bool
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

export const TimeEntry = props => {
  const [entry, setEntry] = useState(props.entry);

  function saveEntity(event, errors, values, num) {
    props.entry.duration = parseTime(values.duration);
    props.entry.description = values.description;
    props.entry.shortDescription = values.shortDescription;
    props.entry.date = values.date;
    axios.put('api/time-entries/', props.entry);
    const clone = JSON.parse(JSON.stringify(props.entry));
    setEntry(clone);
    props.onUpdate ? props.onUpdate() : '';
  }

  return (
    <tr key={props.i}>
      <td>
        <TimeEntryToDuration entities={[entry]}/>
      </td>
      <td>{entry.shortDescription}</td>
      <td>{entry.date}</td>
      <td>
        <div>
          <TimeEntryUpdateModal entity={entry} saveEntity={saveEntity} num={props.i}/>
        </div>
      </td>
    </tr>
  )
}

export const TimeEntries = props => {
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
        props.entries.map((entry, i) => TimeEntry({entry, i}))
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

