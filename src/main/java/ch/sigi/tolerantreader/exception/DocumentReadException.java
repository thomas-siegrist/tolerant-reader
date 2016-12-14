/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader.exception;

public class DocumentReadException extends Exception {
    public DocumentReadException(Throwable t) {
        super(t);
    }

    public DocumentReadException(String s) {
        super(s);
    }
}
