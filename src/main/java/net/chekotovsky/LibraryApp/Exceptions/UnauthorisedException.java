package net.chekotovsky.LibraryApp.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorisedException extends ApiException{
    public UnauthorisedException(String message) {
        super(message, "LIBRARY_UNAUTHORISED");
    }
}
