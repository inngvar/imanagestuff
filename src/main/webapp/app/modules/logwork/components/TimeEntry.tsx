import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { parseTime } from 'app/shared/util/date-utils';
import { TimeEntryToDuration } from 'app/entities/time-entry/time-to-total.tsx';
import { Button } from 'reactstrap';
import TimeEntryUpdateModal from 'app/entities/time-entry/time-entry-modal';
import PropTypes from 'prop-types';

export const TimeEntry = props => {
  const [entry, setEntry] = useState(props.entry);
  const [hideEntry, setHideEntry] = useState(false);

  useEffect(() => {
    setEntry(props.entry);
  }, [props.entry, hideEntry]);

  function saveEntry(event, errors, values, num) {
    if (values.date !== props.entry.date) setHideEntry(true);
    props.entry.duration = parseTime(values.duration);
    props.entry.shortDescription = values.shortDescription;
    props.entry.date = values.date;
    axios.put('api/time-entries/', props.entry).then(response => setEntry(response.data));
  }

  function deleteEntry() {
    if (window.confirm('Delete the item?')) {
      axios.delete('api/time-entries/' + props.entry.id);
      setHideEntry(true);
    }
  }

  return (
    <tr key={props.key} style={hideEntry ? { display: 'none' } : {}}>
      <td>
        <TimeEntryToDuration entities={[entry]} />
      </td>
      <td>{entry.shortDescription}</td>
      <td style={{ textAlign: 'center' }}>{entry.date}</td>
      <td style={{ textAlign: 'center' }}>
        {props.changable && (
          <div>
            <TimeEntryUpdateModal entity={props.entry} saveEntity={saveEntry} num={props.key} />
          </div>
        )}
      </td>
      <td style={{ textAlign: 'center' }}>
        {props.changable && (
          <div>
            <Button color="primary" onClick={deleteEntry}>
              Удалить
            </Button>
          </div>
        )}
      </td>
    </tr>
  );
};

TimeEntry.propTypes = {
  entry: PropTypes.object,
  key: PropTypes.number,
  changable: PropTypes.bool,
  date: PropTypes.string,
};
