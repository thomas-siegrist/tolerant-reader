/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader.exception;

public class TolerantReaderException extends Exception {
    public TolerantReaderException(Throwable t) {
        super(t);
    }

    public TolerantReaderException(String s) {
        super(s);
    }
}
