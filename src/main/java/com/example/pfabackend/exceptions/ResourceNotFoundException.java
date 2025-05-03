// filepath: c:\Users\fatma\Desktop\pfa-3hmat\pfa-backend\src\main\java\com\example\pfabackend\exceptions\ResourceNotFoundException.java
package com.example.pfabackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // Ou HttpStatus.BAD_REQUEST selon la préférence
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
