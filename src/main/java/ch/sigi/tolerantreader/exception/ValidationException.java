/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader.exception;

public class ValidationException extends Exception {
    public ValidationException(Throwable t) {
        super(t);
    }

    public ValidationException(String s) {
        super(s);
    }
}
