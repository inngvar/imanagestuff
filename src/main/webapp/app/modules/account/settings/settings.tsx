import React, { useEffect } from 'react';
import { Button, Col, Alert, Row } from 'reactstrap';
import { connect } from 'react-redux';
import { Translate, translate } from 'react-jhipster';
import { AvForm, AvField } from 'availity-reactstrap-validation';

import { locales, languages } from 'app/config/translation';
import { IRootState } from 'app/shared/reducers';
import { getSession } from 'app/shared/reducers/authentication';
import { saveAccountSettings, reset } from './settings.reducer';
import { generateTelegramLink, reset as resetTelegram } from './telegram.reducer';

export interface IUserSettingsProps extends StateProps, DispatchProps {}

export const SettingsPage = (props: IUserSettingsProps) => {
  useEffect(() => {
    props.getSession();
    const interval = setInterval(() => {
      if (!props.account.telegramId) {
        props.getSession();
      }
    }, 5000); // Poll every 5 seconds if not linked

    return () => {
      props.reset();
      props.resetTelegram();
      clearInterval(interval);
    };
  }, [props.account.telegramId]);

  useEffect(() => {
    if (props.telegramLink) {
      window.open(props.telegramLink, '_blank');
    }
  }, [props.telegramLink]);

  const handleValidSubmit = (event, values) => {
    const account = {
      ...props.account,
      ...values,
    };

    props.saveAccountSettings(account);
    event.persist();
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="settings-title">
            <Translate contentKey="settings.title" interpolate={{ username: props.account.login }}>
              User settings for {props.account.login}
            </Translate>
          </h2>
          <AvForm id="settings-form" onValidSubmit={handleValidSubmit}>
            {/* First name */}
            <AvField
              className="form-control"
              name="firstName"
              label={translate('settings.form.firstname')}
              id="firstName"
              placeholder={translate('settings.form.firstname.placeholder')}
              validate={{
                required: { value: true, errorMessage: translate('settings.messages.validate.firstname.required') },
                minLength: { value: 1, errorMessage: translate('settings.messages.validate.firstname.minlength') },
                maxLength: { value: 50, errorMessage: translate('settings.messages.validate.firstname.maxlength') },
              }}
              value={props.account.firstName}
            />
            {/* Last name */}
            <AvField
              className="form-control"
              name="lastName"
              label={translate('settings.form.lastname')}
              id="lastName"
              placeholder={translate('settings.form.lastname.placeholder')}
              validate={{
                required: { value: true, errorMessage: translate('settings.messages.validate.lastname.required') },
                minLength: { value: 1, errorMessage: translate('settings.messages.validate.lastname.minlength') },
                maxLength: { value: 50, errorMessage: translate('settings.messages.validate.lastname.maxlength') },
              }}
              value={props.account.lastName}
            />
            {/* Email */}
            <AvField
              name="email"
              label={translate('global.form.email.label')}
              placeholder={translate('global.form.email.placeholder')}
              type="email"
              validate={{
                required: { value: true, errorMessage: translate('global.messages.validate.email.required') },
                minLength: { value: 5, errorMessage: translate('global.messages.validate.email.minlength') },
                maxLength: { value: 254, errorMessage: translate('global.messages.validate.email.maxlength') },
              }}
              value={props.account.email}
            />
            {/* Language key */}
            <AvField
              type="select"
              id="langKey"
              name="langKey"
              className="form-control"
              label={translate('settings.form.language')}
              value={props.account.langKey}
            >
              {locales.map(locale => (
                <option value={locale} key={locale}>
                  {languages[locale].name}
                </option>
              ))}
            </AvField>
            <Button color="primary" type="submit">
              <Translate contentKey="settings.form.button">Save</Translate>
            </Button>
          </AvForm>
          <hr />
          <div id="telegram-settings">
            <h3>
              <Translate contentKey="settings.telegram.title">Telegram Bot</Translate>
            </h3>
            <p>
              <Translate contentKey="settings.telegram.explanation">
                Connect Telegram bot to receive reminders about unfilled time and quick work recording.
              </Translate>
            </p>
            {props.account.telegramId ? (
              <Alert color="success">
                <Translate contentKey="settings.telegram.status.linked" interpolate={{ telegramId: props.account.telegramId }}>
                  Your account is linked to Telegram (ID: {props.account.telegramId}).
                </Translate>
              </Alert>
            ) : (
              <Alert color="warning">
                <Translate contentKey="settings.telegram.status.notLinked">Your account is not linked to Telegram.</Translate>
              </Alert>
            )}

            <Button color="info" onClick={props.generateTelegramLink} disabled={props.loading}>
              <Translate contentKey={props.account.telegramId ? 'settings.telegram.relink' : 'settings.telegram.generate'}>
                {props.account.telegramId ? 'Re-link Account' : 'Link Account'}
              </Translate>
            </Button>
          </div>
        </Col>
      </Row>
    </div>
  );
};

const mapStateToProps = ({ authentication, telegram }: IRootState) => ({
  account: authentication.account,
  isAuthenticated: authentication.isAuthenticated,
  telegramLink: telegram.telegramLink,
  expiresAt: telegram.expiresAt,
  loading: telegram.loading,
});

const mapDispatchToProps = { getSession, saveAccountSettings, reset, generateTelegramLink, resetTelegram };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(SettingsPage);
