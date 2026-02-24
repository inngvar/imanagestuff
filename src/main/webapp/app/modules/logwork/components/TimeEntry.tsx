import React from 'react';
import axios from 'axios';
import {TimeEntryToDuration} from 'app/entities/time-entry/time-to-total.tsx';
import {Button} from 'reactstrap';
import TimeEntryUpdateModal from 'app/entities/time-entry/time-entry-modal';
import {ITimeEntry} from "app/shared/model/time-entry.model";

export const TimeEntry = (props: {
  entry: ITimeEntry,
  key: number,
  showButtons: boolean,
  date: string,
  onUpdate: () => void,
  onEdit?: (entry: ITimeEntry) => void,
  useModalEdit?: boolean
}) => {
  function saveEntry(event, errors, values) {
    if (errors?.length) {
      return;
    }
    axios
      .put('api/time-entries/', {
        ...props.entry,
        ...values,
      })
      .then(props.onUpdate);
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
        {props.showButtons && !props.useModalEdit && props.onEdit && (
          <div>
            <Button color="primary" onClick={() => props.onEdit(props.entry)}>
              Изменить
            </Button>
          </div>
        )}
        {props.showButtons && props.useModalEdit && (
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
