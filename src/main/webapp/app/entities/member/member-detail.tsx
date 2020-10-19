import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './member.reducer';
import { IMember } from 'app/shared/model/member.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IMemberDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const MemberDetail = (props: IMemberDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { memberEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2>
          <Translate contentKey="imanagestuffApp.member.detail.title">Member</Translate> [<b>{memberEntity.id}</b>]
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="login">
              <Translate contentKey="imanagestuffApp.member.login">Login</Translate>
            </span>
          </dt>
          <dd>{memberEntity.login}</dd>
          <dt>
            <span id="firstName">
              <Translate contentKey="imanagestuffApp.member.firstName">First Name</Translate>
            </span>
          </dt>
          <dd>{memberEntity.firstName}</dd>
          <dt>
            <span id="middleName">
              <Translate contentKey="imanagestuffApp.member.middleName">Middle Name</Translate>
            </span>
          </dt>
          <dd>{memberEntity.middleName}</dd>
          <dt>
            <span id="lastName">
              <Translate contentKey="imanagestuffApp.member.lastName">Last Name</Translate>
            </span>
          </dt>
          <dd>{memberEntity.lastName}</dd>
        </dl>
        <Button tag={Link} to="/member" replace color="info">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/member/${memberEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ member }: IRootState) => ({
  memberEntity: member.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(MemberDetail);
