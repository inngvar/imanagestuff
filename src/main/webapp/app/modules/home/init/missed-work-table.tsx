import React from "react";
import {Table} from 'reactstrap';
import {Link} from "react-router-dom";
import {IRegisteredTime} from "app/shared/model/day-registered-time.model";
import {IProject} from "app/shared/model/project.model";

export class MissedWorkTable extends React.Component<IRegisteredTime> {
  render() {
    return (
      <Table responsive>
        <tr>
          <th>Дата</th>
          <th>Всего за день</th>
          <th>Потеряно</th>
          <th>Проект</th>
          <th>По проекту</th>
          <th>Найти</th>
        </tr>
        {this.formTableBody()}
      </Table>
    )
  }

  formTableBody() {
    const tableBody = [];
    this.props.dayRegisteredTimes
      .sort((a, b) => {
        const aDate = new Date(a.date);
        const bDate = new Date(b.date);
        return a.date === b.date ? 0 : aDate.getTime() < bDate.getTime() ? 1 : -1;
      })
      .forEach((day) => {
        const spanLen = day.projectDurations.length;
        tableBody.push(<tr>
          <td rowSpan={spanLen + 1}>{day.date}</td>
          <td rowSpan={spanLen + 1}>{this.parseDuration(day.totalDuration)}</td>
          <td rowSpan={spanLen + 1}>{this.getMissedTime(day.totalDuration)}</td>
          {spanLen === 0 && <td>-</td>}
          {spanLen === 0 && <td>-</td>}
          {spanLen === 0 && <td>-</td>}
        </tr>)
        day.projectDurations.forEach((proj) => {
          tableBody.push(<tr>
            <td>{proj?.project?.name}</td>
            <td>{this.parseDuration(proj.duration)}</td>
            {this.projectTimeLogLink(proj?.project, day.date)}
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
      return "-";
    return result.trim();
  }

  getMissedTime(dur: string) {
    const minutes = /([0-9]{1,2})M/.exec(dur) || ["", "0"];
    const hours = /([0-9]{1})H/.exec(dur) || ["", "0"];
    const missedHours = 8 - Number(hours[1]);
    const missedMinutes = (60 - Number(minutes[1])) % 60;
    let result;
    if (missedMinutes !== 0) {
      result = (missedHours - 1) + "H " + missedMinutes + "M";
    } else {
      result = missedHours + "H";
    }
    return result;
  }

  projectTimeLogLink(project: IProject, date: Date) {
    return (
      <td>
        <Link to={"/logwork?project=" + project.id + "&date=" + date}>link</Link>
      </td>
    )
  }
}
