import React from 'react';
import axios from 'axios';
import {parseTime} from 'app/shared/util/date-utils';
import {TimeEntryToDuration} from 'app/entities/time-entry/time-to-total.tsx';
import {Button} from 'reactstrap';
import TimeEntryUpdateModal from 'app/entities/time-entry/time-entry-modal';
import {ITimeEntry} from "app/shared/model/time-entry.model";

export const TimeEntry = (props: {
  entry: ITimeEntry,
  key: number,
  showButtons: boolean,
  date: string,
  onUpdate: () => void
}) => {


  function saveEntry(event, errors, values, num) {
    props.entry.duration = parseTime(values.duration);
    props.entry.shortDescription = values.shortDescription;
    props.entry.date = values.date;
    axios.put('api/time-entries/', props.entry).then(props.onUpdate);
  }

  function deleteEntry() {
    if (window.confirm('Delete the item?')) {
      axios.delete('api/time-entries/' + props.entry.id).then(props.onUpdate);
    }
  }

  return (
    <tr key={props.key}>
      <td>
        <TimeEntryToDuration entities={[props.entry]}/>
      </td>
      <td>{props.entry.shortDescription}</td>
      <td style={{textAlign: 'center'}}>{props.entry.date}</td>
      <td style={{textAlign: 'center'}}>
        {props.showButtons && (
          <div>
            <TimeEntryUpdateModal entity={props.entry} saveEntity={saveEntry} num={props.key}/>
          </div>
        )}
      </td>
      <td style={{textAlign: 'center'}}>
        {props.showButtons && (
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


