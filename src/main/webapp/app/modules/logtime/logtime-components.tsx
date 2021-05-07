import React, {useState, useEffect} from 'react';
import PropTypes from 'prop-types';
import {
  Table
} from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import {APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT} from "app/config/constants";

export const LogTimeTable = props => {

  return (
    <Table className="table-striped table-hover table-sm">
      <thead className="thead-dark">
      <tr>
        <th scope="col">#</th>
        <th scope="col">Участник</th>
        <th scope="col">Дата</th>
        <th scope="col">Время прибытия</th>
        <th scope="col">Время убытия</th>
      </tr>
      </thead>
      <tbody>
      {props.entries ? (
        props.entries.map((entry, i) => (
          <tr key={i}>
            <td>{++i}</td>
            <td>{entry.memberLastName}</td>
            <td>{entry.date ? <TextFormat type="date" value={entry.date} format={APP_LOCAL_DATE_FORMAT} /> : null}</td>
            <td>{entry.checkIn ? <TextFormat type="date" value={entry.checkIn} format={APP_DATE_FORMAT} /> : null}</td>
            <td>{entry.checkOut ? <TextFormat type="date" value={entry.checkOut} format={APP_DATE_FORMAT} /> : null}</td>
          </tr>
        ))
      ) : (
        <p>No time logs found</p>
      )}
      </tbody>
    </Table>
  );
}

LogTimeTable.propTypes = {
  entries: PropTypes.arrayOf(PropTypes.object)
}

