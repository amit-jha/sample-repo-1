package com.aj.searchapi.exception;

public class RootException extends Exception{
    public RootException() {
    }

    public RootException(String message) {
        super(message);
    }

    public RootException(String message, Throwable cause) {
        super(message, cause);
    }

    public RootException(Throwable cause) {
        super(cause);
    }
}
