import React from 'react';
import { useSelector } from 'react-redux';
import { TimeEntryToDuration } from 'app/entities/time-entry/time-to-total.tsx';
import { Table } from 'reactstrap';
import PropTypes from 'prop-types';
import { TimeEntry } from './TimeEntry';

export const TimeEntries = props => {
  const account = useSelector(state => state['authentication'].account);
  return (
    <Table className="table-striped table-hover table-sm" responsive>
      <thead className="thead-dark">
        <tr>
          <th scope="col" style={{ width: '150px', textAlign: 'center' }}>
            Часы
          </th>
          <th scope="col" style={{ width: '50%', textAlign: 'center' }}>
            Описание
          </th>
          <th scope="col" style={{ width: '100px', textAlign: 'center' }}>
            Дата
          </th>
          <th scope="col" style={{ width: '100px' }} />
          <th scope="col" style={{ width: '100px' }} />
        </tr>
      </thead>
      <tbody>
        {props.entries ? (
          props.entries.map((entry, i) => (
            <TimeEntry entry={entry} key={i} date={entry.date} changable={account.login === entry.memberLogin} />
          ))
        ) : (
          <p>No Tasks</p>
        )}
      </tbody>
      <tfoot>
        <td colSpan={2}>
          <h5>
            Всего : <TimeEntryToDuration entities={props.entries} />
          </h5>
        </td>
      </tfoot>
    </Table>
  );
};

TimeEntries.propTypes = {
  entries: PropTypes.array,
};
