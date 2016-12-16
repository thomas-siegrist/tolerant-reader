/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader.validation;

import ch.sigi.tolerantreader.exception.ValidationException;
import ch.sigi.tolerantreader.model.Node;

public interface Validator {

    /**
     * Validates the nodeValue with the given Information about the given Node.
     * 
     * @throws ValidationException In case the validation cannot be fulfilled.
     */
    void validate(Object nodeValue, Node node) throws ValidationException;

}
