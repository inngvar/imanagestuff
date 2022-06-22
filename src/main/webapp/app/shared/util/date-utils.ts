import moment from 'moment';
import { parse, end, toSeconds, pattern } from 'iso8601-duration';

import { APP_LOCAL_DATETIME_FORMAT, APP_LOCAL_DATETIME_FORMAT_Z, APP_LOCAL_DATETIME_FORMAT_ZONED } from 'app/config/constants';

function roundToTwo(num) {
  return Math.round((num + Number.EPSILON) * 100) / 100;
}

export const convertDateTimeFromServer = date => (date ? moment(date).format(APP_LOCAL_DATETIME_FORMAT) : null);

export const convertDateTimeToServer = date => (date ? moment(date, APP_LOCAL_DATETIME_FORMAT_Z).toDate() : null);

export const displayDefaultDateTime = () => moment().startOf('day').format(APP_LOCAL_DATETIME_FORMAT);

export const durationToHours = duration => roundToTwo(toSeconds(parse(duration)) / 60 / 60);

export const roundNumberToTwo = num => roundToTwo(num);

export const formatDateTimeWithZone = date => moment(date).format(APP_LOCAL_DATETIME_FORMAT_ZONED);

export const formatDuration = durationToFormat => {
  return durationToFormat
    .toUpperCase()
    .replace(/\s/g, '')
    .replace(/В/g, 'D')
    .replace(/Д/g, 'D')
    .replace(/Р/g, 'H')
    .replace(/Ч/g, 'H')
    .replace(/Ь/g, 'M')
    .replace(/М/g, 'M');
};

export const parseTime = (time: string) => {
  time = formatDuration(time);
  const minutes = /[0-9]{1,2}M/.exec(time) || [''];
  const hours = /[0-9]{1}H/.exec(time) || [''];
  const days = /[0-9]{1}D/.exec(time) || [''];
  const result = (days[0] && '8H') || hours[0] + minutes[0] || '0S';
  return 'PT' + result.trim();
};
