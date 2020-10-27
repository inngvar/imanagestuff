import axios from 'axios';
import React, {useState, useEffect} from 'react';
import {
  Button,
  Row,
  Col
} from 'reactstrap';
import {TimeEntries} from "app/modules/logwork/logwork-components";
import {TimeEntryToDuration} from "app/entities/time-entry/time-to-total.tsx";


export const ProjectReport = props => {

  const [projectStats, setProjectStats] = useState(null);
  const [updating, setUpdating] = useState(false);
  useEffect(() => {
    if (!props.project) {
      return;
    }
    axios.get("api/reports/project/" + props.project.id + '?dateFrom=' + props.dateFrom + '&dateTo=' + props.dateTo).then(response => {
      setProjectStats(response.data);
    });
  }, [props.project, props.dateFrom, props.dateTo]);

  function sendReport() {
    axios.post('api/reports/day-report/'+props.project.id + '?dateFrom=' + props.dateFrom + '&dateTo=' + props.dateTo);
  }

  const timeEntities = membersReports => {
    let result = [];
    if(membersReports) {
      for (let i = 0; i < membersReports.length; i++) {
        const res = result.concat(membersReports[i].entries);
        result = res;
      }
    }
    return result;
  }

  function onUpdateTotalTime() {
    setUpdating(!updating)
  }

  return (
    <Row>
      <Col md="12">
        {
          projectStats?.membersReports ? (projectStats.membersReports.map((memberStats, i) => (
              <Row key={i}>
                <h3>{memberStats.member.fio}</h3>
                <TimeEntries entries={memberStats.entries} onUpdate={onUpdateTotalTime} member={memberStats.member}/>
              </Row>
            )))
            :
            (<p>noData</p>)
        }
        <Row>
          <Button onClick={()=>sendReport()}>Отправить отчёт</Button>
        </Row>
        <Row>
          <h3><TimeEntryToDuration entities={timeEntities(projectStats?.membersReports)} added='Всего по проекту : '/></h3>
        </Row>
      </Col>
    </Row>
  );
}
