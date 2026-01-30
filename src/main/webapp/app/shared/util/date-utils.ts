import moment from 'moment';
import { parse, end, toSeconds, pattern } from 'iso8601-duration';

import { APP_LOCAL_DATETIME_FORMAT, APP_LOCAL_DATETIME_FORMAT_Z, APP_LOCAL_DATETIME_FORMAT_ZONED } from 'app/config/constants';

function roundToTwo(num: number) {
  return Math.round((num + Number.EPSILON) * 100) / 100;
}

export const convertDateTimeFromServer = date => (date ? moment(date).format(APP_LOCAL_DATETIME_FORMAT) : null);

export const convertDateTimeToServer = date => (date ? moment(date, APP_LOCAL_DATETIME_FORMAT_Z).toDate() : null);

export const displayDefaultDateTime = () => moment().startOf('day').format(APP_LOCAL_DATETIME_FORMAT);

export const durationToHours = (duration: string) => roundToTwo(toSeconds(parse(duration)) / 60 / 60);

export const roundNumberToTwo = (num: number) => roundToTwo(num);

export const formatDateTimeWithZone = (date: string) => moment(date).format(APP_LOCAL_DATETIME_FORMAT_ZONED);

export const formatDuration = (durationToFormat: string) => {
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
  const hours = /[0-9]H/.exec(time) || [''];
  const days = /[0-9]D/.exec(time) || [''];
  const result = (days[0] && '8H') || hours[0] + minutes[0] || '0S';
  return 'PT' + result.trim();
};

// Validates if a duration string is in a valid format (PT... or user-friendly format)
export const isValidDuration = (duration: string): boolean => {
  if (!duration || typeof duration !== 'string') {
    return false;
  }

  const trimmed = duration.trim();

  // Check if it's already in ISO 8601 format (PT...)
  if (trimmed.startsWith('PT')) {
    try {
      parse(trimmed);
      return true;
    } catch {
      return false;
    }
  }

  // Check for user-friendly formats like "2h 30m", "2:30", "90m", "2h"
  // Valid patterns:
  // - "2h30m" or "2h 30m" or "2:30"
  // - "90m" or "90 min"
  // - "2h" or "2 hours"
  const userFriendlyPattern = /^(\d+h\s*)?(\d+m\s*)?(\d+s\s*)?$/i;
  const timePattern = /^(\d{1,2}):(\d{1,2})$/; // HH:MM format

  const formatted = formatDuration(trimmed);

  // Check if it matches user-friendly pattern after formatting
  if (userFriendlyPattern.test(formatted)) {
    return true;
  }

  // Check if it matches time pattern (HH:MM)
  if (timePattern.test(trimmed)) {
    return true;
  }

  return false;
};

// Converts user-friendly duration input to ISO 8601 format (PT...)
// Handles inputs like: "2h 30m", "2:30", "90m", "2h", "PT6H"
export const convertToIsoDuration = (input: string): string => {
  if (!input || typeof input !== 'string') {
    return null;
  }

  const trimmed = input.trim();

  // If already in ISO 8601 format, validate and return
  if (trimmed.startsWith('PT')) {
    try {
      parse(trimmed);
      return trimmed;
    } catch {
      throw new Error('Invalid ISO 8601 duration format');
    }
  }

  // Handle HH:MM format (e.g., "2:30" -> PT2H30M)
  const timePattern = /^(\d{1,2}):(\d{1,2})$/;
  const timeMatch = timePattern.exec(trimmed);
  if (timeMatch) {
    const hours = parseInt(timeMatch[1], 10);
    const minutes = parseInt(timeMatch[2], 10);
    if (minutes >= 60) {
      throw new Error('Minutes must be less than 60');
    }
    return `PT${hours}H${minutes}M`;
  }

  // Format the input and parse it
  const formatted = formatDuration(trimmed);

  // Extract components
  let hours = 0;
  let minutes = 0;
  let seconds = 0;

  // Match hours
  const hourMatch = /(\d+)H/.exec(formatted);
  if (hourMatch) {
    hours = parseInt(hourMatch[1], 10);
  }

  // Match minutes
  const minuteMatch = /(\d+)M/.exec(formatted);
  if (minuteMatch) {
    minutes = parseInt(minuteMatch[1], 10);
  }

  // Match seconds
  const secondMatch = /(\d+)S/.exec(formatted);
  if (secondMatch) {
    seconds = parseInt(secondMatch[1], 10);
  }

  // If no components found, check if it's just a number (interpreted as minutes)
  if (hours === 0 && minutes === 0 && seconds === 0) {
    const numericMatch = /^(\d+)$/.exec(trimmed);
    if (numericMatch) {
      minutes = parseInt(numericMatch[1], 10);
    } else {
      throw new Error('Invalid duration format. Use formats like: "2h 30m", "2:30", "90m", or "2h"');
    }
  }

  // Validate minutes
  if (minutes >= 60) {
    throw new Error('Minutes must be less than 60. Use hours instead.');
  }

  // Build ISO 8601 string
  let result = 'PT';
  if (hours > 0) result += `${hours}H`;
  if (minutes > 0) result += `${minutes}M`;
  if (seconds > 0) result += `${seconds}S`;

  // If all zeros, return PT0S
  if (result === 'PT') {
    return 'PT0S';
  }

  // Validate the result
  try {
    parse(result);
    return result;
  } catch {
    throw new Error('Invalid duration format');
  }
};

// Converts ISO 8601 duration to a user-friendly display format (e.g., "2h 30m")
export const formatDurationForDisplay = (duration: string): string => {
  if (!duration) return '';

  try {
    const parsed = parse(duration);
    const hours = parsed.hours || 0;
    const minutes = parsed.minutes || 0;

    const parts = [];
    if (hours > 0) parts.push(`${hours}ч`);
    if (minutes > 0) parts.push(`${minutes}м`);

    return parts.join(' ') || '0ч';
  } catch {
    return duration;
  }
};

// Converts ISO 8601 duration to total minutes (for backend storage)
export const durationToMinutes = (duration: string): number => {
  if (!duration) return 0;

  try {
    const seconds = toSeconds(parse(duration));
    return Math.round(seconds / 60);
  } catch {
    return 0;
  }
};

// Converts minutes to ISO 8601 duration format
export const minutesToDuration = (minutes: number): string => {
  if (minutes <= 0) return 'PT0S';

  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;

  let result = 'PT';
  if (hours > 0) result += `${hours}H`;
  if (mins > 0) result += `${mins}M`;

  return result;
};
