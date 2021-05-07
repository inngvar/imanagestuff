import React, {useState, useEffect} from 'react';
import {connect} from 'react-redux';
import axios from 'axios';
import {
  Button,
  Row,
  Col
} from 'reactstrap';
import {Translate, translate} from 'react-jhipster';
import {toast} from 'react-toastify';
import {LogTimeTable} from "app/modules/logtime/logtime-components";

export type ILogTimeProp = StateProps;

enum TimeAction {
  CHECK_IN,
  CHECK_OUT
}

export const LogTime = (props: ILogTimeProp) => {

  const {account} = props;
  const [currentMember, setCurrentMember] = useState(null);
  const [isShowLogModal, setIsShowLogModal] = useState(false);
  const [isShowReportModal, setIsShowReportModal] = useState(false);
  const [isEnableButtons, setIsEnableButtons] = useState(false);
  const [entries, setEntries] = useState(null);

  const getLogs = () => {
    axios.get("api/time-logs").then(response => {
      setEntries(response.data)
    })
  }

  useEffect(() => {
    if (!account.login) {
      return;
    }
    axios.get("api/projects/current").then(response => {
      const mem = response.data[0].members.find(m => m.login === account.login);
      setCurrentMember(mem);
      getLogs();
    })
  }, [account])

  useEffect(() => {
    setIsEnableButtons(!!currentMember)
  }, [currentMember])

  const toggleLogModal = () => {
    setIsShowLogModal(!isShowLogModal)
  };

  const toggleReportModal = () => {
    setIsShowReportModal(!isShowReportModal)
  };

  const sendTime = (action: TimeAction) => {
    const url = action === TimeAction.CHECK_IN ? `api/time-logs/${currentMember.id}/checkin` : `api/time-logs/${currentMember.id}/checkout`;

    setIsEnableButtons(false);

    const reason = action === TimeAction.CHECK_IN ? "о прибытии" : "об убытии";
    axios.post(url, null, {headers: {['Content-Type']: 'application/json'}}).then(response => {

      toast.success(`Отметка ${reason} успешно сохранена!`, {
        position: "top-center",
        autoClose: 3000,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: false,
        draggable: true,
        progress: undefined,
      });

      setIsEnableButtons(true);
      getLogs();

    }).catch(err => {
      toast.error(`Ошибка при сохранении отметки: ${err.message}`, {
          position: "top-center",
          hideProgressBar: false,
          closeOnClick: true,
          pauseOnHover: false,
        }
      );

      setIsEnableButtons(true);
    })
  }

  const sendCheckIn = () => {
    sendTime(TimeAction.CHECK_IN);
  }

  const sendCheckOut = () => {
    sendTime(TimeAction.CHECK_OUT);
  }

  return (
    <div className="container-fluid">
      <Row>
        <Col md="9">
          <h2>
            <Translate contentKey="logtime.title">Log Your Work Every Day</Translate>
          </h2>
          <p className="lead">
            <Translate contentKey="logwork.subtitle">This is your duty</Translate>
          </p>
          <Button color="success" onClick={sendCheckIn} disabled={!isEnableButtons}>Отметить прибытие</Button>
          &nbsp;&nbsp;&nbsp;&nbsp;
          <Button color="success" onClick={sendCheckOut} disabled={!isEnableButtons}>Отметить убытие</Button>
          &nbsp;&nbsp;&nbsp;&nbsp;
          <Button color="warning" disabled={!isEnableButtons}>Сформировать отчет</Button>
        </Col>
      </Row>
      <br/>
      <LogTimeTable
        entries={entries}
      />
      {/*<LogTimeModal
        toggle={toggleLogModal}
        onSave={saveLog}
        isShow={isShowLogModal}
      />*/}
    </div>
  )

}

const mapStateToProps = storeState => ({
  account: storeState.authentication.account,
  isAuthenticated: storeState.authentication.isAuthenticated,
});

type StateProps = ReturnType<typeof mapStateToProps>;

export default connect(mapStateToProps)(LogTime)
