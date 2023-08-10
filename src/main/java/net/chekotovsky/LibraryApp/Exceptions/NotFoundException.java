package net.chekotovsky.LibraryApp.Exceptions;

public class NotFoundException extends ApiException{
    public NotFoundException(String message, String errorCode) {
        super(message, errorCode);
    }
}
