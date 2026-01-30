import axios from 'axios';
import '../home/home.scss';
import './logwork.scss';
import React, { useState, useEffect } from 'react';
import { durationToHours, formatDuration, convertToIsoDuration, isValidDuration } from 'app/shared/util/date-utils';
import { Translate, translate } from 'react-jhipster';
import { connect } from 'react-redux';
import { MemberList, ProjectList, TimeEntries } from 'app/modules/logwork/logwork-components';
import { Button, Row, Col, Form, FormGroup, Label, Input } from 'reactstrap';
import { cleanEntity } from 'app/shared/util/entity-utils';

export type ILogWorkProp = StateProps;

export const LogWork = (props: ILogWorkProp) => {
  const { account } = props;
  const [projects, setProjects] = useState([]);
  const [isDefaultProject, setIsDefaultProject] = useState(false);
  const [currentProject, setCurrentProject] = useState(null);
  const [currentMember, setCurrentMember] = useState(null);
  const [reportDate, setReportDate] = useState(new Date().toISOString().substr(0, 10));
  const [entries, setEntries] = useState(null);
  const [duration, setDuration] = useState(null);
  const [entryDescription, setEntryDescription] = useState('');
  const [errorMessage, setErrorMessage] = useState(null);
  const maxLengthDescription = 256;
  const queryParams = (() => {
    const query = window.location.search.substring(1);
    const vars = query.split('&');
    const result = {};
    for (let i = 0; i < vars.length; i++) {
      const pair = vars[i].split('=');
      result[pair[0]] = pair[1];
    }
    return result;
  })();

  useEffect(() => {
    if (!account.login) {
      return;
    }
    axios.get('api/projects/current').then(response => {
      setProjects(response.data);
      const mem = response.data[0].members.find(m => m.login === account.login);
      const dProject = response.data.find(p => p.id === mem.defaultProjectId);
      if (queryParams['project']) {
        setCurrentProject(response.data.find(p => p.id === parseInt(queryParams['project'], 10)));
      } else {
        setCurrentProject(dProject ? dProject : response.data[0]);
      }
      if (queryParams['date']) {
        setReportDate(queryParams['date']);
      }
      setCurrentMember(mem);
    });
  }, [account]);

  useEffect(() => {
    setIsDefaultProject(currentProject?.id === currentMember?.defaultProjectId);
  });

  function updateEntries() {
    axios.get('api/time-entries/of/' + currentMember.id + '/in/' + currentProject.id + '?date=' + reportDate).then(response => {
      setEntries(response.data);
    });
  }

  useEffect(() => {
    if (!currentMember || !reportDate) {
      return;
    }
    updateEntries();
  }, [currentMember, currentProject, reportDate]);

  const updateCurrentProject = selectedProject => {
    setCurrentProject(selectedProject);
  };

  const updateCurrentMember = member => {
    setCurrentMember(member);
  };

  const addNewEntry = () => {
    // Validate duration input
    if (!duration || !isValidDuration(duration)) {
      setErrorMessage(translate('imanagestuffApp.timeEntry.validation.invalidDuration'));
      return;
    }

    try {
      const isoDuration = convertToIsoDuration(duration);
      const entity = {
        duration: isoDuration,
        shortDescription: entryDescription,
        projectId: currentProject.id,
        memberId: currentMember.id,
        date: reportDate,
      };
      axios
        .post('api/time-entries/', cleanEntity(entity))
        .then(result => {
          updateEntries();
          setEntryDescription('');
          setDuration('');
          setErrorMessage(null);
        })
        .catch(error => {
          if (error.response) {
            setErrorMessage(error.response.data);
          }
        });
    } catch (error) {
      setErrorMessage(error.message);
    }
  };

  const updateDefaultProjectForMembers = () => {
    const mem = currentMember;
    mem.defaultProjectId = currentProject.id;
    mem.defaultProjectName = currentProject.name;
    axios.put('/api/members', mem);
    setIsDefaultProject(true);
  };

  return (
    <Row>
      <Col md="9">
        <h2>
          <Translate contentKey="logwork.title">Log Your Work Every Day</Translate>
        </h2>
        <p className="lead">
          <Translate contentKey="logwork.subtitle">This is your duty</Translate>
        </p>
        {currentMember && <h5>{currentMember?.firstName + ' ' + currentMember?.lastName + '(' + currentMember.login + ')'}</h5>}
        <Row class="form-row">
          <ProjectList
            projects={projects}
            value={currentProject}
            handler={updateCurrentProject}
            isDefaultProject={isDefaultProject}
            updateDefaultProject={updateDefaultProjectForMembers}
            showButton={true}
          />
          <MemberList project={currentProject} value={currentMember} handler={updateCurrentMember} />
          <FormGroup className="col-sm">
            <Label>Дата:</Label>
            <Input
              type="date"
              name="reportDate"
              class-name="form-control"
              defaultValue={reportDate}
              value={reportDate}
              onChange={event => setReportDate(event.target.value)}
            />
          </FormGroup>
        </Row>
        {currentMember?.login === account?.login && (
          <Col>
            <Row>
              <Form className="jumbotron">
                <h3>Добавить задачу</h3>
                <Row className="align-items-center">
                  <FormGroup className="col-auto">
                    <Label className="sr-only" for="description">
                      Описание
                    </Label>
                    <textarea
                      name="description"
                      id="description"
                      className="form-control logwork"
                      placeholder="Описание"
                      maxLength={maxLengthDescription}
                      value={entryDescription}
                      onChange={event => setEntryDescription(event.target.value)}
                    ></textarea>
                    <p style={{ textAlign: 'right' }}>
                      {' '}
                      {entryDescription.length} / {maxLengthDescription}{' '}
                    </p>
                  </FormGroup>
                  <FormGroup className="col-auto">
                    <Label className="sr-only" for={'logwork'}>
                      Время
                    </Label>
                    <input
                      type="text"
                      name="logwork"
                      className="form-control"
                      id="logwork"
                      placeholder="Время"
                      value={duration}
                      onChange={e => setDuration(e.target.value)}
                    />
                  </FormGroup>
                  <FormGroup className="col-auto">
                    <Button
                      className="btn-primary"
                      onClick={event => {
                        addNewEntry();
                        return false;
                      }}
                    >
                      +
                    </Button>
                  </FormGroup>
                </Row>
                <Row>
                  <div>{errorMessage}</div>
                </Row>
              </Form>
            </Row>
          </Col>
        )}
        <Row>
          <TimeEntries entries={entries} />
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
