import axios from 'axios';
import '../home/home.scss';
import React, {useState, useEffect} from 'react';
import {Link} from 'react-router-dom';
import {Translate} from 'react-jhipster';
import {connect} from 'react-redux';
import {MemberList, ProjectList, TimeEntries} from "app/modules/logwork/logwork-components";
import {
  Dropdown,
  DropdownMenu,
  DropdownItem,
  DropdownToggle,
  Row,
  Col,
  Form,
  FormGroup,
  Input,
  Label,
  Container
} from 'reactstrap';
import {IRootState} from 'app/shared/reducers';
import project from "app/entities/project/project";

export type ILogWorkProp = StateProps;

export const LogWork = (props: ILogWorkProp) => {
  const {account} = props;
  const [projects, setProjects] = useState([]);
  const [currentProject, setCurrentProject] = useState({id: 'initial', members: []});
  const [currentMember, setCurrentMember] = useState({id: ''});
  const [reportDate, setReportDate] = useState(new Date().toISOString().substr(0, 10));
  const [entries, setEntries] = useState(null);

  useEffect(() => {
    if (!account.login) {
      return;
    }
    axios.get("api/projects/current").then(response => {
      setProjects(response.data);
      setCurrentProject(response.data[0]);
      const mem = response.data[0].members.find(m => m.login === account.login);
      setCurrentMember(mem);
    })
  }, [account])

  useEffect(() => {
    if (!currentMember || !reportDate) {
      return;
    }
    axios.get("api/time-entries/of/" + currentMember.id + "?date=" + reportDate).then(response => {
      setEntries(response.data);
    });
  }, [currentMember, reportDate])

  const updateCurrentProject = selectedProject => {
    setCurrentProject(selectedProject);
    const mem = selectedProject.members.find(member => member.login === account.login);
    setCurrentMember(mem);
  }

  const updateCurrentMember = member => {
    setCurrentMember(member);
  }

  return (
    <Row>
      <p>CURRENT PROJECT:{currentProject ? currentProject.id : ''}</p>
      <Col md="9">
        <h2>
          <Translate contentKey="logwork.title">Log Your Work Every Day</Translate>
        </h2>
        <p className="lead">
          <Translate contentKey="logwork.subtitle">This is your duty</Translate>
        </p>
        <Form>
          <FormGroup>
            <Label>Пользователь</Label>
            <input type="text" value={account.login} readOnly={true}/>
          </FormGroup>
          <ProjectList projects={projects} value={currentProject} handler={updateCurrentProject}/>
          <MemberList project={currentProject} value={currentMember} handler={updateCurrentMember}/>
          <FormGroup>
            <Label>Дата:</Label>
            <input type="date" name="reportDate" class-name="form-control" defaultValue={reportDate} value={reportDate}
                   onChange={event => setReportDate(event.target.value)}/>
          </FormGroup>
        </Form>
        <Row>
          <TimeEntries entries={entries}/>
        </Row>
      </Col>
    </Row>
  );
};

const mapStateToProps = storeState => ({
  account: storeState.authentication.account,
  isAuthenticated: storeState.authentication.isAuthenticated,
});

type StateProps = ReturnType<typeof mapStateToProps>;

export default connect(mapStateToProps)(LogWork);
