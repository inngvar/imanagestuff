import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { ITimeLog, defaultValue } from 'app/shared/model/time-log.model';

export const ACTION_TYPES = {
  FETCH_TIMELOG_LIST: 'timeLog/FETCH_TIMELOG_LIST',
  FETCH_TIMELOG: 'timeLog/FETCH_TIMELOG',
  CREATE_TIMELOG: 'timeLog/CREATE_TIMELOG',
  UPDATE_TIMELOG: 'timeLog/UPDATE_TIMELOG',
  DELETE_TIMELOG: 'timeLog/DELETE_TIMELOG',
  RESET: 'timeLog/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<ITimeLog>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type TimeLogState = Readonly<typeof initialState>;

// Reducer

export default (state: TimeLogState = initialState, action): TimeLogState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_TIMELOG_LIST):
    case REQUEST(ACTION_TYPES.FETCH_TIMELOG):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_TIMELOG):
    case REQUEST(ACTION_TYPES.UPDATE_TIMELOG):
    case REQUEST(ACTION_TYPES.DELETE_TIMELOG):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_TIMELOG_LIST):
    case FAILURE(ACTION_TYPES.FETCH_TIMELOG):
    case FAILURE(ACTION_TYPES.CREATE_TIMELOG):
    case FAILURE(ACTION_TYPES.UPDATE_TIMELOG):
    case FAILURE(ACTION_TYPES.DELETE_TIMELOG):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_TIMELOG_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10),
      };
    case SUCCESS(ACTION_TYPES.FETCH_TIMELOG):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_TIMELOG):
    case SUCCESS(ACTION_TYPES.UPDATE_TIMELOG):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_TIMELOG):
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

const apiUrl = 'api/time-logs';

// Actions

export const getEntities: ICrudGetAllAction<ITimeLog> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_TIMELOG_LIST,
    payload: axios.get<ITimeLog>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`),
  };
};

export const getEntity: ICrudGetAction<ITimeLog> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_TIMELOG,
    payload: axios.get<ITimeLog>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<ITimeLog> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_TIMELOG,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<ITimeLog> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_TIMELOG,
    payload: axios.put(apiUrl, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<ITimeLog> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_TIMELOG,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
