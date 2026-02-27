package org.manage.web.rest.errors;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class AlertException extends WebApplicationException {
    private static final long serialVersionUID = 1L;

    private final String message;
    private final String entityName;
    private final String errorKey;

    public AlertException(String message, String entityName, String errorKey, Response.Status status) {
        super(Response
            .status(status)
            .entity(message)
            .header("message", "error." + errorKey)
            .header("params", entityName)
        .build());
        this.message = message;
        this.entityName = entityName;
        this.errorKey = errorKey;
    }
}
