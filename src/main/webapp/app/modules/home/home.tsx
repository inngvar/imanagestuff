import './home.scss';

import axios from 'axios';
import React, {useEffect, useState} from 'react';
import {Link} from 'react-router-dom';
import {Translate} from 'react-jhipster';
import {connect} from 'react-redux';
import {Row, Col, Alert} from 'reactstrap';

import {IDayRegisteredTime} from "app/shared/model/day-registered-time.model";
import {MissedWorkTable} from "app/modules/home/init/missed-work-table";

export type IHomeProp = StateProps;

export const Home = (props: IHomeProp) => {
  const {account} = props;
  const [missedWorkLog, setMissedWorkLog] = useState<Array<IDayRegisteredTime>>([]);
  const NUMBER_OF_DAYS = 14;

  useEffect(() => {
    if (account && account.login) {
      const url = "api/reports/registered-time-report/" + account.login + '/' + NUMBER_OF_DAYS;
      axios.get(url).then(response => {
        setMissedWorkLog(response.data);
      });
    }
  }, [account]);

  return (
    <Row>
      <Col md="9">

        {account && account.login ? (
          <div>
            <h2>
              <Translate contentKey="home.title">Добро пожаловать снова</Translate> <b>{account.login}</b>
            </h2>
            <div>
              <div>
                <h1>Потерянное время</h1>
                <MissedWorkTable dayRegisteredTimes={missedWorkLog}></MissedWorkTable>
              </div>
            </div>
          </div>
        ) : (
          <div>
            <h2>
              <Translate contentKey="home.title">Добро пожаловать снова</Translate>
            </h2>
            <Alert>
              <p>Для использования приложения вам необходимо &nbsp;
                <Link to="/login" className="alert-link">
                  <Translate contentKey="global.messages.info.authenticated.link">Войти</Translate>
                </Link>
              </p>
            </Alert>
            <Alert>
              <Translate contentKey="global.messages.info.register.noaccount">Нет аккаунта,
                зарегистрируйтесь?</Translate>&nbsp;
              <Link to="/account/register" className="alert-link">
                <Translate contentKey="global.messages.info.register.link">Register a new account</Translate>
              </Link>
            </Alert>
          </div>
        )}
      </Col>
      <Col md="3" className="pad">
        <span className="hipster rounded"/>
      </Col>
    </Row>
  );
};

const mapStateToProps = storeState => ({
  account: storeState.authentication.account,
  isAuthenticated: storeState.authentication.isAuthenticated,
});

type StateProps = ReturnType<typeof mapStateToProps>;

export default connect(mapStateToProps)(Home);
