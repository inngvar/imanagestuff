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
import { useSelector } from 'react-redux';

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
    <FormGroup className="col-sm">
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
  const [hideEntry, setHideEntry] = useState(false)

  useEffect(() => {
    setEntry(props.entry)
  }, [props.entry, hideEntry]);

  function saveEntry(event, errors, values, num) {
    if (values.date !== props.entry.date)
      setHideEntry(true);
    props.entry.duration = parseTime(values.duration);
    props.entry.shortDescription = values.shortDescription;
    props.entry.date = values.date;
    axios.put('api/time-entries/', props.entry).then((response) => setEntry(response.data));
  }

  function deleteEntry() {
    if (window.confirm("Delete the item?")) {
      axios.delete('api/time-entries/' + props.entry.id);
      setHideEntry(true);
    }
  }

  return (
    <tr key={props.key} style={hideEntry ? {display: "none"} : {}}>
      <td>
        <TimeEntryToDuration entities={[entry]}/>
      </td>
      <td>{entry.shortDescription}</td>
      <td>{entry.date}</td>
      <td>
        {props.changable &&
          <div>
            <TimeEntryUpdateModal entity={props.entry} saveEntity={saveEntry} num={props.key}/>
          </div>
        }
      </td>
      <td>
        {props.changable &&
          <div>
            <Button color="primary" onClick={deleteEntry}>Удалить</Button>
          </div>
        }
      </td>
    </tr>
  )
}

export const TimeEntries = props => {
  const account = useSelector((state) => state["authentication"].account)
  return (
    <Table className="table-striped table-hover table-sm">
      <thead className="thead-dark">
      <tr>
        <th scope="col">Часы</th>
        <th scope="col">Описание</th>
        <th scope="col">Дата</th>
        <th scope="col"/>
        <th scope="col"/>
      </tr>
      </thead>
      <tbody>
      {props.entries ? (
        props.entries.map((entry, i) => <TimeEntry entry={entry} key={i} date={entry.date}
                                                   changable={account.login === entry.memberLogin}/>)
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

