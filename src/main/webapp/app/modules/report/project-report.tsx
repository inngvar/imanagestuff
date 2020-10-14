import axios from 'axios';
import React, {useState, useEffect} from 'react';
import {roundNumberToTwo} from 'app/shared/util/date-utils';
import {
  Button,
  Row,
  Col,
  Form,
  FormGroup,
  Label,
} from 'reactstrap';
import {TimeEntries} from "app/modules/logwork/logwork-components";


export const ProjectReport = props => {

  const [projectStats, setProjectStats] = useState(null);
  useEffect(() => {
    if (!props.project) {
      return;
    }
    axios.get("api/reports/project/" + props.project.id + '?dateFrom=' + props.dateFrom + '&dateTo=' + props.dateTo).then(response => {
      setProjectStats(response.data);
    });
  }, [props.project, props.dateFrom, props.dateTo]);

  function sendReport(from, to) {
    axios.post('api/reports/day-report', {
      projectId: props.project.id,
      fromDate: from,
      toDate: to
    })
  }

  return (
    <Row>
      <Col md="12">
        {
          projectStats?.membersReports ? (projectStats.membersReports.map((memberStats, i) => (
              <Row key={i}>
                <h3>{memberStats.member.fio}</h3>
                <TimeEntries entries={memberStats.entries}/>
              </Row>
            )))
            :
            (<p>noData</p>)
        }
        <Row>
          <Button onClick={e => {
            sendReport(props.dateFrom ,props.dateTo);
            return false;
          }}>Отправить отчёт</Button>
        </Row>
        <Row>
          <h3>
            Итого часов по проекту {roundNumberToTwo(projectStats?.totalHours)}
          </h3>
        </Row>
      </Col>
    </Row>

  );
}
