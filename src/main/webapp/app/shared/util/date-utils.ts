import moment from 'moment';
import { parse, end, toSeconds, pattern } from 'iso8601-duration';

import { APP_LOCAL_DATETIME_FORMAT, APP_LOCAL_DATETIME_FORMAT_Z } from 'app/config/constants';

function roundToTwo(num) {
  return Math.round((num + Number.EPSILON) * 100) / 100;
}

export const convertDateTimeFromServer = date => (date ? moment(date).format(APP_LOCAL_DATETIME_FORMAT) : null);

export const convertDateTimeToServer = date => (date ? moment(date, APP_LOCAL_DATETIME_FORMAT_Z).toDate() : null);

export const displayDefaultDateTime = () => moment().startOf('day').format(APP_LOCAL_DATETIME_FORMAT);

export const durationToHours = duration => roundToTwo(toSeconds(parse(duration)) / 60 / 60);

export const roundNumberToTwo = num => roundToTwo(num);
