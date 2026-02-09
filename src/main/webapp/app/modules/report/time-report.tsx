import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import { Row, Col, Form, FormGroup, Label, Button, Table } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { LogTimeReportModal } from '../logtime/logtime-modal';
import { toast } from 'react-toastify';
import './report.scss';

export const TimeReport = props => {
  const [dateFrom, setDateFrom] = useState(new Date().toISOString().substr(0, 10));
  const [dateTo, setDateTo] = useState(new Date().toISOString().substr(0, 10));
  const [reportData, setReportData] = useState(null);
  const [isShowModal, setIsShowModal] = useState(false);

  const getReportData = () => {
    axios.get(`api/reports/time-logs/?dateFrom=${dateFrom}&dateTo=${dateTo}`).then(response => {
      setReportData(response.data);
    });
  };

  useEffect(() => {
    getReportData();
  }, [dateFrom, dateTo]);

  const sendReport = (event, errors, values) => {
    if (errors && errors.length > 0) {
      return;
    }

    if (!values.addresses) {
      return;
    }

    let url = `api/reports/time-report?dateFrom=${dateFrom}&dateTo=${dateTo}`;

    values.addresses.split(',').forEach(addr => {
      url += `&to=${addr.trim()}`;
    });

    axios
      .post(url, null, { headers: { ['Content-Type']: 'application/json' } })
      .then(response => {
        toast.success(`Отчет успешно отправлен!`, {
          position: 'top-center',
          autoClose: 3000,
          hideProgressBar: false,
          closeOnClick: true,
          pauseOnHover: false,
          draggable: true,
          progress: undefined,
        });

        setIsShowModal(false);
      })
      .catch(err => {
        toast.error(`Ошибка при отправке отчета: ${err.message}`, {
          position: 'top-center',
          hideProgressBar: false,
          closeOnClick: true,
          pauseOnHover: false,
        });
      });
  };

  const toggle = () => {
    setIsShowModal(!isShowModal);
  };

  return (
    <div>
      <Col md="12">
        <Form className="report-form">
          <Row class="form-row"></Row>
          <FormGroup className="date-inputs">
            <Label>Дата:&nbsp;</Label>
            <input
              type="date"
              name="dateFrom"
              className="form-control"
              defaultValue={dateFrom}
              value={dateFrom}
              onChange={event => setDateFrom(event.target.value)}
            />
            <Label>по&nbsp;</Label>
            <input
              type="date"
              name="dateTo"
              className="form-control"
              defaultValue={dateTo}
              value={dateTo}
              onChange={event => setDateTo(event.target.value)}
            />
          </FormGroup>
        </Form>
        <Row>
          <Col md="12">
            <Row>
              <Table className="table-striped table-hover table-sm">
                <thead className="thead-dark">
                  <tr>
                    <th scope="col">#</th>
                    <th scope="col">Участник</th>
                    <th scope="col">Время, ч</th>
                    <th scope="col">Дней с отметками</th>
                  </tr>
                </thead>
                <tbody>
                  {reportData?.membersReports ? (
                    reportData.membersReports.map((membersReport, i) => (
                      <tr key={i}>
                        <td>{i + 1}</td>
                        <td>{membersReport.member.fio}</td>
                        <td>{membersReport.totalHours}</td>
                        <td>{membersReport.entries.length}</td>
                      </tr>
                    ))
                  ) : (
                    <div></div>
                  )}
                </tbody>
                <tr></tr>
              </Table>
            </Row>
            <Row>
              <Button color="danger" onClick={toggle}>
                Отправить отчёт
              </Button>
            </Row>
          </Col>
        </Row>
      </Col>
      <LogTimeReportModal toggle={toggle} isShow={isShowModal} onSend={sendReport} />
    </div>
  );
};

const mapStateToProps = storeState => ({
  account: storeState.authentication.account,
  isAuthenticated: storeState.authentication.isAuthenticated,
});

type StateProps = ReturnType<typeof mapStateToProps>;

export default connect(mapStateToProps)(TimeReport);
