import axios from 'axios';
import '../home/home.scss';
import React, {useState, useEffect} from 'react';
import {connect} from 'react-redux';
import {ProjectList} from "app/modules/logwork/logwork-components";
import {
  Row,
  Col,
  Form,
  FormGroup,
  Label,
} from 'reactstrap';
import {ProjectReport} from "app/modules/report/project-report";

export type ILogWorkProp = StateProps;

export const WorkReport = (props: ILogWorkProp) => {
  const {account} = props;
  const [projects, setProjects] = useState([]);
  const [currentProject, setCurrentProject] = useState(null);
  const [dateFrom, setDateFrom] = useState(new Date().toISOString().substr(0, 10));
  const [dateTo, setDateTo] = useState(new Date().toISOString().substr(0, 10));

  useEffect(() => {
    if (!account.login) {
      return;
    }
    axios.get("api/projects/current").then(response => {
      setProjects(response.data);
      const mem = response.data[0].members.find(m => m.login === account.login);
      const dProject = response.data.find(p => p.id === mem.defaultProjectId)
      setCurrentProject(dProject ? dProject : response.data[0]);
    })
  }, [account])

  const updateCurrentProject = selectedProject => {
    setCurrentProject(selectedProject);
  }

  return (
    <Col md="12">
      <Form>
        <Row class="form-row">
          <ProjectList projects={projects} value={currentProject} handler={updateCurrentProject}/>
        </Row>
        <FormGroup>
          <Label>Дата: </Label>
          <input type="date" name="dateFrom" class-name="form-control" defaultValue={dateFrom} value={dateFrom}
                 onChange={event => setDateFrom(event.target.value)}/>
          <Label>по </Label>
          <input type="date" name="dateTo" class-name="form-control" defaultValue={dateTo} value={dateTo}
                 onChange={event => setDateTo(event.target.value)}/>
        </FormGroup>
      </Form>
      <Row>
        <Col md="12">
          <ProjectReport project={currentProject} dateFrom={dateFrom} dateTo={dateTo} ></ProjectReport>
        </Col>
      </Row>
    </Col>
  );
};

const mapStateToProps = storeState => ({
  account: storeState.authentication.account,
  isAuthenticated: storeState.authentication.isAuthenticated,
});

type StateProps = ReturnType<typeof mapStateToProps>;

export default connect(mapStateToProps)(WorkReport);
