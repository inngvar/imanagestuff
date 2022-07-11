import React, {useState, useEffect} from "react";
import {Table, Row} from 'reactstrap';
import axios from 'axios';
import Moment from 'react-moment';
import {connect} from 'react-redux';
import {Link} from "react-router-dom";
import {IDayRegisteredTime, IProjectDuration, IRegisteredTime} from "app/shared/model/day-registered-time.model";
import {IProject} from "app/shared/model/project.model";
import {Home} from "app/modules/home/home";
import moment from 'moment'


function parseDuration(dur: number): string {
  const minutes = dur % 60;
  let h = 0;
  if (dur > minutes) {
    h = (dur - minutes) / 60;
  }
  let result = '';
  if (h > 0) {
    result = result + h.toString() + "ч";
  }
  if (minutes > 0) {
    result = result + " " + minutes + "м";
  }
  if (result.length === 0) {
    result = "0ч";
  }
  return result;
}

function projectTimeLogLink(projects: Array<IProjectDuration>, date: Date) {
  if (projects.length > 0) {
    return (
      projects.map((project, i) => (
        <Link key={`entity-${i}`} to={"/logwork?project=" + project.project.id + "&date=" + date}>Отметить
          в {project.project.name}</Link>
      )))
  } else {
    return (<Link to={"/logwork?" + "date=" + date}>Отметить в этот день</Link>)
  }
}


export const MissedWorkTable = props => {

  const NUMBER_OF_DAYS = 14;

  const {account} = props;

  const [missedWorkLog, setMissedWorkLog] = useState<Array<IDayRegisteredTime>>([]);

  useEffect(() => {
    if (account && account.login) {
      const url = "api/reports/registered-time-report/" + account.login + '/' + NUMBER_OF_DAYS;
      axios.get(url).then(response => {
        setMissedWorkLog(response.data);
      });
    }
  }, [account]);

  return (<Row>
    <Table>
      <tr>
        <th>День недели</th>
        <th>Дата</th>
        <th>Отмечено</th>
        <th>Осталось</th>
        <th>Найти</th>
      </tr>
      <tbody>
      {missedWorkLog.map((log, i) => (
        <tr key={`entity-${i}`}>
          <td><Moment format="dddd">{log.date}</Moment></td>
          <td><Moment format="DD/MM/YYYY">{log.date}</Moment></td>
          <td>{parseDuration(log.totalDuration)}</td>
          <td>{parseDuration(log.unregisteredDuration)}</td>
          <td>{projectTimeLogLink(log.projectDurations, log.date)}</td>
        </tr>
      ))}
      </tbody>
    </Table>
  </Row>);

}
