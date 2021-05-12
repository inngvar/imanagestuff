import React from 'react';
import {translate, Translate} from 'react-jhipster';

import {NavItem, NavLink, NavbarBrand} from 'reactstrap';
import {NavLink as Link} from 'react-router-dom';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import appConfig from 'app/config/constants';
import {NavDropdown} from "app/shared/layout/menus/menu-components";
import MenuItem from "app/shared/layout/menus/menu-item";

export const BrandIcon = props => (
  <div {...props} className="brand-icon">
    <img src="content/images/logo-jhipster.png" alt="Logo"/>
  </div>
);

export const Brand = props => (
  <NavbarBrand tag={Link} to="/" className="brand-logo">
    <BrandIcon/>
    <span className="brand-title">
      <Translate contentKey="global.title">Imanagestuff</Translate>
    </span>
    <span className="navbar-version">{appConfig.VERSION}</span>
  </NavbarBrand>
);

export const Home = props => (
  <NavItem>
    <NavLink tag={Link} to="/" className="d-flex align-items-center">
      <FontAwesomeIcon icon="home"/>
      <span>
        <Translate contentKey="global.menu.home">Home</Translate>
      </span>
    </NavLink>
  </NavItem>
);

export const LogWork = props => (
  <NavItem>
    <NavLink tag={Link} to="/logwork" className="d-flex align-items-center">
      <FontAwesomeIcon icon="user-clock"/>
      <span>
          <Translate contentKey="global.menu.worklog">WorkLog</Translate>
      </span>
    </NavLink>
  </NavItem>
);
export const ReportMenu = props => (
  <NavDropdown
    name={translate("global.menu.reports")}
    id="reports-menu"
    style={{maxHeight: '80vh', overflow: 'auto'}}
  >
    <MenuItem icon="asterisk" to="/report">
      <FontAwesomeIcon icon="info"/>
      <span>
          <Translate contentKey="global.menu.report">WorkLog</Translate>
      </span>
    </MenuItem>
    <MenuItem icon="asterisk" to="/time-report">
      <FontAwesomeIcon icon="info"/>
      <span>
          <Translate contentKey="global.menu.time-report">TimeLog</Translate>
      </span>
    </MenuItem>
  </NavDropdown>
);

export const LogTimeMenuItem = props => (
  <NavItem>
    <NavLink tag={Link} to="/logtime" className="d-flex align-items-center">
      <FontAwesomeIcon icon="user-clock"/>
      <span>
          <Translate contentKey="global.menu.checkin">TimeLog</Translate>
      </span>
    </NavLink>
  </NavItem>
);
