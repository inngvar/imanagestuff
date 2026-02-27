package org.manage.web.rest.errors;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

public class NotFoundAlertException extends AlertException {

    public NotFoundAlertException(String message, String entityName, String errorKey) {
        super(message, entityName, errorKey, NOT_FOUND);
    }
}
