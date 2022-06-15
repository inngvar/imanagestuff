import React from 'react';
import { parse, end, toSeconds, pattern } from 'iso8601-duration';


export const TimeEntryToDuration = props => {
  const { entities, added } = props;

  function decOfNum(number, titles) {
    const cases = [2, 0, 1, 1, 1, 2];
    return titles[(number % 100 > 4 && number % 100 < 20) ? 2 : cases[(number % 10 < 5) ? number % 10 : 5]];
  }

  let hours = 0;
  let minutes = 0;

  if (entities) {
    for (let i = 0; i < entities.length; i++) {
      const time = parse(entities[i].duration);
      hours += time.hours;
      minutes += time.minutes;
    }
  }

  if(minutes >= 60){
    hours += Math.trunc( minutes / 60);
    minutes = minutes % 60;
  }

  let result = hours > 0 ? hours + ' ' + decOfNum(hours, ['час', 'часа', 'часов']) : '';
  result += minutes > 0 ? ' ' + minutes + ' ' + decOfNum(minutes, ['минута', 'минуты', 'минут']) : '';

  return (
    <span>{added}&nbsp;{result ?result.trim() : '0 часов и 0 минут' }</span>
  );
}
