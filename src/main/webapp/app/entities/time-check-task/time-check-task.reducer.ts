import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { ITimeCheckTask, defaultValue } from 'app/shared/model/time-check-task.model';

export const ACTION_TYPES = {
  FETCH_TIMECHECKTASK_LIST: 'timeCheckTask/FETCH_TIMECHECKTASK_LIST',
  FETCH_TIMECHECKTASK: 'timeCheckTask/FETCH_TIMECHECKTASK',
  CREATE_TIMECHECKTASK: 'timeCheckTask/CREATE_TIMECHECKTASK',
  UPDATE_TIMECHECKTASK: 'timeCheckTask/UPDATE_TIMECHECKTASK',
  DELETE_TIMECHECKTASK: 'timeCheckTask/DELETE_TIMECHECKTASK',
  RESET: 'timeCheckTask/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<ITimeCheckTask>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type TimeCheckTaskState = Readonly<typeof initialState>;

// Reducer

export default (state: TimeCheckTaskState = initialState, action): TimeCheckTaskState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_TIMECHECKTASK_LIST):
    case REQUEST(ACTION_TYPES.FETCH_TIMECHECKTASK):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_TIMECHECKTASK):
    case REQUEST(ACTION_TYPES.UPDATE_TIMECHECKTASK):
    case REQUEST(ACTION_TYPES.DELETE_TIMECHECKTASK):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_TIMECHECKTASK_LIST):
    case FAILURE(ACTION_TYPES.FETCH_TIMECHECKTASK):
    case FAILURE(ACTION_TYPES.CREATE_TIMECHECKTASK):
    case FAILURE(ACTION_TYPES.UPDATE_TIMECHECKTASK):
    case FAILURE(ACTION_TYPES.DELETE_TIMECHECKTASK):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_TIMECHECKTASK_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10),
      };
    case SUCCESS(ACTION_TYPES.FETCH_TIMECHECKTASK):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_TIMECHECKTASK):
    case SUCCESS(ACTION_TYPES.UPDATE_TIMECHECKTASK):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_TIMECHECKTASK):
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

const apiUrl = 'api/time-check-tasks';

// Actions

export const getEntities: ICrudGetAllAction<ITimeCheckTask> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_TIMECHECKTASK_LIST,
    payload: axios.get<ITimeCheckTask>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`),
  };
};

export const getEntity: ICrudGetAction<ITimeCheckTask> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_TIMECHECKTASK,
    payload: axios.get<ITimeCheckTask>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<ITimeCheckTask> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_TIMECHECKTASK,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<ITimeCheckTask> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_TIMECHECKTASK,
    payload: axios.put(apiUrl, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<ITimeCheckTask> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_TIMECHECKTASK,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
