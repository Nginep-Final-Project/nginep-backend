package com.example.nginep.exceptions.notFoundException;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Setter
@Getter
public class NotFoundException extends RuntimeException {
  private HttpStatus httpStatus;
  private List<String> errors;
  private Object data;

  public NotFoundException(HttpStatus httpStatus, String message, List<String> errors, Object data) {
    super(message);

    this.httpStatus = httpStatus;
    this.errors = errors;
    this.data = data;
  }

  public NotFoundException(HttpStatus httpStatus, String message) {
    this(httpStatus, message, Collections.singletonList(message), null);
  }

  public NotFoundException(String message) {this(HttpStatus.NOT_FOUND, message);}
}
