package net.chekotovsky.LibraryApp.Exceptions;

public class DuplicateException extends ApiException{
    public DuplicateException(String message, String errorCode) {
        super(message, errorCode);
    }
}
