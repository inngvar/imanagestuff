import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';
import telegram, { ACTION_TYPES, reset } from 'app/modules/account/settings/telegram.reducer';

describe('Telegram reducer tests', () => {
  const initialState = {
    loading: false,
    telegramLink: null,
    expiresAt: null,
    errorMessage: null,
  };

  it('should return the initial state', () => {
    expect(telegram(undefined, {})).toEqual(initialState);
  });

  it('should reset the state', () => {
    expect(
      telegram(
        {
          ...initialState,
          loading: true,
          telegramLink: 'http://t.me/bot',
        },
        reset()
      )
    ).toEqual(initialState);
  });

  it('should set loading to true on REQUEST', () => {
    expect(
      telegram(initialState, {
        type: REQUEST(ACTION_TYPES.GENERATE_LINK),
      })
    ).toEqual({
      ...initialState,
      loading: true,
    });
  });

  it('should set data on SUCCESS', () => {
    const payload = {
      data: {
        link: 'http://t.me/bot?start=123',
        expiresAt: '2023-01-01T00:00:00Z',
      },
    };
    expect(
      telegram(initialState, {
        type: SUCCESS(ACTION_TYPES.GENERATE_LINK),
        payload,
      })
    ).toEqual({
      ...initialState,
      loading: false,
      telegramLink: payload.data.link,
      expiresAt: payload.data.expiresAt,
    });
  });

  it('should set error on FAILURE', () => {
    const payload = 'error message';
    expect(
      telegram(initialState, {
        type: FAILURE(ACTION_TYPES.GENERATE_LINK),
        payload,
      })
    ).toEqual({
      ...initialState,
      loading: false,
      errorMessage: payload,
    });
  });
});
