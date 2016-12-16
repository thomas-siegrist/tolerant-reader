/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader.validation.impl;

import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;

import ch.sigi.tolerantreader.exception.ValidationException;
import ch.sigi.tolerantreader.model.Node;
import ch.sigi.tolerantreader.validation.Validator;

public class DefaultValidator implements Validator {

    @Override
    public void validate(Object nodeValue, Node node) throws ValidationException {
        validateNotNullConstraint(nodeValue, node);
        validatePatternConstraint(nodeValue, node);
    }

    private void validateNotNullConstraint(Object nodeValue, Node node) throws ValidationException {
        NotNull notNullAnnotation = node.getValidateNotNull();
        if (notNullAnnotation != null && nodeValue == null) {
            throw new ValidationException("The Field [" + node.getName() + "] is declared with @NotNull, but it is null.");
        }
    }

    private void validatePatternConstraint(Object nodeValue, Node node) throws ValidationException {
        javax.validation.constraints.Pattern patternAnnotation = node.getValidateRegex();
        if (nodeValue == null || patternAnnotation == null)
            return;

        Pattern pattern = Pattern.compile(patternAnnotation.regexp());
        if (!pattern.matcher(String.valueOf(nodeValue)).matches()) {
            throw new ValidationException(
                    "The Field [" + node.getName() + "] is declared with @Pattern, but it does not match the given Pattern. Value: " + nodeValue + " Pattern: " + patternAnnotation.regexp());
        }
    }

}
