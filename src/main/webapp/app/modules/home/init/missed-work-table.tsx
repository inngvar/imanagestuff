import React from "react";
import {Table} from 'reactstrap';

import {IRegisteredTime} from "app/shared/model/day-registered-time.model";

export class MissedWorkTable extends React.Component<IRegisteredTime> {
  render() {
    return(
      <Table responsive>
        <tr>
          <th>Дата</th>
          <th>Всего за день</th>
          <th>Проект</th>
          <th>По проекту</th>
        </tr>
        {this.formTableBody()}
    </Table>
    )
  }

  formTableBody(){
    const tableBody = [];
    this.props.dayRegisteredTimes
      .sort((a,b) => {
        const aDate = new Date(a.date);
        const bDate = new Date(b.date);
        return a.date === b.date ? 0 : aDate.getTime() < bDate.getTime() ? 1 : -1;
      })
      .forEach((day) => {
      const spanLen = day.projectDurations.length;
      tableBody.push(<tr>
        <td rowSpan={spanLen + 1}>{day.date}</td>
        <td rowSpan={spanLen + 1}>{this.parseDuration(day.totalDuration)}</td>
        {spanLen === 0 ? <td></td> : <span hidden={true}></span>}
        {spanLen === 0 ? <td></td> : <span hidden={true}></span>}
      </tr>)
      day.projectDurations.forEach((proj) => {
        tableBody.push(<tr>
          <td>{proj.project.name}</td>
          <td>{this.parseDuration(proj.duration)}</td>
        </tr>)
      })
    });
    return <tbody>{tableBody}</tbody>
  }

  parseDuration(dur: string) {
    const minutes = /[0-9]{1,2}M/.exec(dur) || [""];
    const hours = /[0-9]{1}H/.exec(dur) || [""];
    const result = hours[0] + " " + minutes[0];
    if (" " === result)
      return "0H";
    return result.trim();
  }
}
