package org.manage.web.rest.errors;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

public class BadRequestAlertException extends AlertException {

    public BadRequestAlertException(String message, String entityName, String errorKey) {
        super(message, entityName, errorKey, BAD_REQUEST);
    }
}
