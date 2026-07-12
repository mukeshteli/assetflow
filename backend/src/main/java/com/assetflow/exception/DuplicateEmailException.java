package com.assetflow.exception;

import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends ApiException {

    public DuplicateEmailException(String email) {
        super("An account with email '" + email + "' already exists", HttpStatus.CONFLICT);
    }
}