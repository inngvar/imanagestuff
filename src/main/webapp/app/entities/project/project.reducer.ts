import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IProject, defaultValue } from 'app/shared/model/project.model';

export const ACTION_TYPES = {
  FETCH_PROJECT_LIST: 'project/FETCH_PROJECT_LIST',
  FETCH_PROJECT: 'project/FETCH_PROJECT',
  CREATE_PROJECT: 'project/CREATE_PROJECT',
  UPDATE_PROJECT: 'project/UPDATE_PROJECT',
  DELETE_PROJECT: 'project/DELETE_PROJECT',
  RESET: 'project/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IProject>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type ProjectState = Readonly<typeof initialState>;

// Reducer

export default (state: ProjectState = initialState, action): ProjectState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_PROJECT_LIST):
    case REQUEST(ACTION_TYPES.FETCH_PROJECT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_PROJECT):
    case REQUEST(ACTION_TYPES.UPDATE_PROJECT):
    case REQUEST(ACTION_TYPES.DELETE_PROJECT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_PROJECT_LIST):
    case FAILURE(ACTION_TYPES.FETCH_PROJECT):
    case FAILURE(ACTION_TYPES.CREATE_PROJECT):
    case FAILURE(ACTION_TYPES.UPDATE_PROJECT):
    case FAILURE(ACTION_TYPES.DELETE_PROJECT):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_PROJECT_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10),
      };
    case SUCCESS(ACTION_TYPES.FETCH_PROJECT):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_PROJECT):
    case SUCCESS(ACTION_TYPES.UPDATE_PROJECT):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_PROJECT):
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

const apiUrl = 'api/projects';

// Actions

export const getEntities: ICrudGetAllAction<IProject> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_PROJECT_LIST,
    payload: axios.get<IProject>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`),
  };
};

export const getEntity: ICrudGetAction<IProject> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_PROJECT,
    payload: axios.get<IProject>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IProject> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_PROJECT,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IProject> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_PROJECT,
    payload: axios.put(apiUrl, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IProject> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_PROJECT,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
