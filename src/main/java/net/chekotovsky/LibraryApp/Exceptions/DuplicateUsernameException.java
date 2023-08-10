package net.chekotovsky.LibraryApp.Exceptions;

public class DuplicateUsernameException extends  ApiException{
    public DuplicateUsernameException(String message) {
        super(message, "LIBRARY_DUPLICATE_USERNAME");
    }
}
