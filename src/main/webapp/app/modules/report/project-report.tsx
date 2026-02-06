import axios from 'axios';
import React, {useEffect, useState} from 'react';
import {Button, Col, Row} from 'reactstrap';
import {TimeEntries} from "app/modules/logwork/logwork-components";
import {TimeEntryToDuration} from "app/entities/time-entry/time-to-total.tsx";


export const ProjectReport = props => {

  const [projectStats, setProjectStats] = useState(null);
  const [currentMember, setCurrentMember] = useState(null);

  function updateProjectStats() {
    axios.get("api/reports/project/" + props.project.id + '?dateFrom=' + props.dateFrom + '&dateTo=' + props.dateTo).then(response => {
      setProjectStats(response.data);
    });
  }

  useEffect(() => {
    if (!props.project) {
      return;
    }
    updateProjectStats();
  }, [props.project, props.dateFrom, props.dateTo]);

  useEffect(() => {
    axios.get("api/members/current").then(response => {
      setCurrentMember(response.data);
    });
  }, [])



  function sendReport() {
    axios.post('api/reports/day-report/' + props.project.id + '?dateFrom=' + props.dateFrom + '&dateTo=' + props.dateTo).then(response => {
      if (response.status === 202) {
        alert("Отчет отправлен")
      } else {
        alert("Ошибка отправления")
      }
    });
  }

  const timeEntities = membersReports => {
    let result = [];
    if (membersReports) {
      for (let i = 0; i < membersReports.length; i++) {
        result = result.concat(membersReports[i].entries);
      }
    }
    return result;
  }

  return (
    <Row>
      <Col md="12">
        {
          projectStats?.membersReports ? (projectStats.membersReports
              .sort((a, b) => {
                if (a.member.id === currentMember.id)
                  return -1;
                if (b.member.id === currentMember.id)
                  return 1;
                return 0;
              })
              .map((memberStats, i) => (
                <Row key={i}>
                  <h3>{memberStats.member.fio}</h3>
                  <TimeEntries entries={memberStats.entries} onUpdate={updateProjectStats}/>
                </Row>
              )))
            :
            (<p>noData</p>)
        }
        <Row>
          <Button onClick={() => sendReport()}>Отправить отчёт</Button>
        </Row>
        <Row>
          <h3>
            Всего по проекту : <TimeEntryToDuration entities={timeEntities(projectStats?.membersReports)}/>
          </h3>
        </Row>
      </Col>
    </Row>

  );
}
