package com.assetflow.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(String resource, Object identifier) {
        super(resource + " not found with identifier: " + identifier, HttpStatus.NOT_FOUND);
    }
}