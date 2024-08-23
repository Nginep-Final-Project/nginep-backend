package com.example.nginep.exceptions.duplicateException;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Setter
@Getter
public class DuplicateException extends RuntimeException {
    private HttpStatus httpStatus;
    private List<String> errors;
    private Object data;

    public DuplicateException(HttpStatus httpStatus, String message, List<String> errors, Object data) {
        super(message);

        this.httpStatus = httpStatus;
        this.errors = errors;
        this.data = data;
    }

    public DuplicateException(HttpStatus httpStatus, String message) {
        this(httpStatus, message, Collections.singletonList(message), null);
    }

    public DuplicateException(String message) {this(HttpStatus.FOUND, message);}
}
