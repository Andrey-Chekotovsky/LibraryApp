package net.chekotovsky.LibraryApp.Exceptions;

public class AuthException extends ApiException{
    public AuthException(String message, String errorCode) {
        super(message, errorCode);
    }
}
