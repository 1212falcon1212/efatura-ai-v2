package com.efaturaai.api.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ProblemExceptionHandler {

  @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
  public ProblemDetail handleValidation(Exception ex) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    pd.setTitle("Validation failed");
    pd.setDetail(ex.getMessage());
    pd.setType(java.net.URI.create("about:blank"));
    pd.setProperty("error", "validation_error");
    return pd;
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ProblemDetail handleAuth(UsernameNotFoundException ex) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
    pd.setTitle("Unauthorized");
    pd.setDetail(ex.getMessage());
    return pd;
  }

  @ExceptionHandler(ErrorResponseException.class)
  public ProblemDetail handleErrorResponse(ErrorResponseException ex) {
    return ex.getBody();
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGeneric(Exception ex) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    pd.setTitle("Internal Server Error");
    pd.setDetail(ex.getMessage());
    return pd;
  }
}
