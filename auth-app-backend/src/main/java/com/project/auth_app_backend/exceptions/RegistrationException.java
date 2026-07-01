package com.project.auth_app_backend.exceptions;

public class RegistrationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public RegistrationException(String message) {
        super(message);
    }
}
