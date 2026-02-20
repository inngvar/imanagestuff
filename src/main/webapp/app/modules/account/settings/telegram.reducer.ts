import axios from 'axios';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

export const ACTION_TYPES = {
  GENERATE_LINK: 'telegram/GENERATE_LINK',
  RESET: 'telegram/RESET',
};

const initialState = {
  loading: false,
  telegramLink: null as string,
  expiresAt: null as string,
  errorMessage: null,
};

export type TelegramState = Readonly<typeof initialState>;

// Reducer
export default (state: TelegramState = initialState, action): TelegramState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.GENERATE_LINK):
      return {
        ...state,
        errorMessage: null,
        loading: true,
      };
    case FAILURE(ACTION_TYPES.GENERATE_LINK):
      return {
        ...state,
        loading: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.GENERATE_LINK):
      return {
        ...state,
        loading: false,
        telegramLink: action.payload.data.link,
        expiresAt: action.payload.data.expiresAt,
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState,
      };
    default:
      return state;
  }
};

// Actions
const apiUrl = 'api/telegram/generate-link';

export const generateTelegramLink = () => ({
  type: ACTION_TYPES.GENERATE_LINK,
  payload: axios.post(apiUrl),
});

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
