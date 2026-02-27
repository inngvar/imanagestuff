package org.manage.service.exception;

public class EmailNotFoundException extends RuntimeException {

    public EmailNotFoundException() {
        super("Email address not registered");
    }
}
