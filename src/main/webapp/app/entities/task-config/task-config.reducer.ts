import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { ITaskConfig, defaultValue } from 'app/shared/model/task-config.model';

export const ACTION_TYPES = {
  FETCH_TASKCONFIG_LIST: 'taskConfig/FETCH_TASKCONFIG_LIST',
  FETCH_TASKCONFIG: 'taskConfig/FETCH_TASKCONFIG',
  CREATE_TASKCONFIG: 'taskConfig/CREATE_TASKCONFIG',
  UPDATE_TASKCONFIG: 'taskConfig/UPDATE_TASKCONFIG',
  DELETE_TASKCONFIG: 'taskConfig/DELETE_TASKCONFIG',
  RESET: 'taskConfig/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<ITaskConfig>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type TaskConfigState = Readonly<typeof initialState>;

// Reducer

export default (state: TaskConfigState = initialState, action): TaskConfigState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_TASKCONFIG_LIST):
    case REQUEST(ACTION_TYPES.FETCH_TASKCONFIG):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_TASKCONFIG):
    case REQUEST(ACTION_TYPES.UPDATE_TASKCONFIG):
    case REQUEST(ACTION_TYPES.DELETE_TASKCONFIG):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_TASKCONFIG_LIST):
    case FAILURE(ACTION_TYPES.FETCH_TASKCONFIG):
    case FAILURE(ACTION_TYPES.CREATE_TASKCONFIG):
    case FAILURE(ACTION_TYPES.UPDATE_TASKCONFIG):
    case FAILURE(ACTION_TYPES.DELETE_TASKCONFIG):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_TASKCONFIG_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10),
      };
    case SUCCESS(ACTION_TYPES.FETCH_TASKCONFIG):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_TASKCONFIG):
    case SUCCESS(ACTION_TYPES.UPDATE_TASKCONFIG):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_TASKCONFIG):
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

const apiUrl = 'api/task-configs';

// Actions

export const getEntities: ICrudGetAllAction<ITaskConfig> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_TASKCONFIG_LIST,
    payload: axios.get<ITaskConfig>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`),
  };
};

export const getEntity: ICrudGetAction<ITaskConfig> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_TASKCONFIG,
    payload: axios.get<ITaskConfig>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<ITaskConfig> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_TASKCONFIG,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<ITaskConfig> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_TASKCONFIG,
    payload: axios.put(apiUrl, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<ITaskConfig> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_TASKCONFIG,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
