import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { ITimeEntry, defaultValue } from 'app/shared/model/time-entry.model';

export const ACTION_TYPES = {
  FETCH_TIMEENTRY_LIST: 'timeEntry/FETCH_TIMEENTRY_LIST',
  FETCH_TIMEENTRY: 'timeEntry/FETCH_TIMEENTRY',
  CREATE_TIMEENTRY: 'timeEntry/CREATE_TIMEENTRY',
  UPDATE_TIMEENTRY: 'timeEntry/UPDATE_TIMEENTRY',
  DELETE_TIMEENTRY: 'timeEntry/DELETE_TIMEENTRY',
  RESET: 'timeEntry/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<ITimeEntry>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type TimeEntryState = Readonly<typeof initialState>;

// Reducer

export default (state: TimeEntryState = initialState, action): TimeEntryState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_TIMEENTRY_LIST):
    case REQUEST(ACTION_TYPES.FETCH_TIMEENTRY):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_TIMEENTRY):
    case REQUEST(ACTION_TYPES.UPDATE_TIMEENTRY):
    case REQUEST(ACTION_TYPES.DELETE_TIMEENTRY):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_TIMEENTRY_LIST):
    case FAILURE(ACTION_TYPES.FETCH_TIMEENTRY):
    case FAILURE(ACTION_TYPES.CREATE_TIMEENTRY):
    case FAILURE(ACTION_TYPES.UPDATE_TIMEENTRY):
    case FAILURE(ACTION_TYPES.DELETE_TIMEENTRY):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_TIMEENTRY_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10),
      };
    case SUCCESS(ACTION_TYPES.FETCH_TIMEENTRY):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_TIMEENTRY):
    case SUCCESS(ACTION_TYPES.UPDATE_TIMEENTRY):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_TIMEENTRY):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {},
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState,
      };
    default:
      return state;
  }
};

const apiUrl = 'api/time-entries';

// Actions

export const getEntities: ICrudGetAllAction<ITimeEntry> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_TIMEENTRY_LIST,
    payload: axios.get<ITimeEntry>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`),
  };
};

export const getEntity: ICrudGetAction<ITimeEntry> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_TIMEENTRY,
    payload: axios.get<ITimeEntry>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<ITimeEntry> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_TIMEENTRY,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<ITimeEntry> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_TIMEENTRY,
    payload: axios.put(apiUrl, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<ITimeEntry> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_TIMEENTRY,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
