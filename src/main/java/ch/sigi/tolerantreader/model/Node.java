/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader.model;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import ch.sigi.tolerantreader.annotation.NotNull;
import ch.sigi.tolerantreader.annotation.Pattern;
import org.apache.commons.lang3.Validate;

import ch.sigi.tolerantreader.annotation.CustomName;
import ch.sigi.tolerantreader.annotation.CustomPath;

public class Node implements Serializable {

    private final String name;
    private final Class type;
    private final Class genericType;
    private final CustomPath customPath;
    private final CustomName customName;
    private final Pattern validateRegex;
    private final NotNull validateNotNull;

    /**
     * @param name
     * @param type not <code>null</code>
     * @param genericType nullable
     * @param customPath nullable
     * @param customName nullable
     */
    private Node(String name, Class type, Class genericType, CustomPath customPath, CustomName customName, Pattern validateRegex, NotNull validateNotNull) {
        this.name = name;
        this.type = Validate.notNull(type);
        this.genericType = genericType;
        this.customPath = customPath;
        this.customName = customName;
        this.validateRegex = validateRegex;
        this.validateNotNull = validateNotNull;
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return type;
    }

    public Class getGenericType() {
        return genericType;
    }

    public CustomPath getCustomPath() {
        return customPath;
    }

    public CustomName getCustomName() {
        return customName;
    }

    public Pattern getValidateRegex() {
        return validateRegex;
    }

    public NotNull getValidateNotNull() {
        return validateNotNull;
    }

    /**
     * ******************************************************************************************************
     * The Builder
     * ******************************************************************************************************
     */
    public static final class Builder {

        private String name;
        private Class type;
        private Class genericType;
        private CustomPath customPath;
        private CustomName customName;
        private Pattern validateRegex;
        private NotNull validateNotNull;

        /**
         * @param field not <code>null</code>
         */
        public static Builder forField(Field field) {
            Builder builder = new Builder();
            builder.name = field.getName();
            builder.type = Validate.notNull(field.getType());
            builder.genericType = getGenericTypeClass(field);
            builder.customPath = getAnnotationOfType(field, CustomPath.class);
            builder.customName = getAnnotationOfType(field, CustomName.class);
            builder.validateRegex = getAnnotationOfType(field, Pattern.class);
            builder.validateNotNull = getAnnotationOfType(field, NotNull.class);

            if (builder.customName != null && builder.customPath != null) {
                throw new IllegalArgumentException("You can only use ONE Annotation out of [@CustomName, @CustomPath] per field.");
            }

            return builder;
        }

        private static <T extends Annotation> T getAnnotationOfType(Field field, Class<T> annotationType) {
            T[] annotations = field.getAnnotationsByType(annotationType);
            if (annotations == null || annotations.length == 0)
                return null;
            return annotations[0];
        }

        /**
         * @param type not <code>null</code>
         * @param name not <code>null</code>
         */
        public static Builder forTypeAndName(Class type, String name) {
            Builder builder = new Builder();
            builder.type = Validate.notNull(type);
            builder.name = name;
            return builder;
        }

        public Node build() {
            return new Node(
                    name,
                    type,
                    genericType,
                    customPath,
                    customName,
                    validateRegex,
                    validateNotNull);
        }

        private static Class getGenericTypeClass(Field field) {
            Type returnType = field.getGenericType();
            if (returnType instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) returnType;
                Type[] argTypes = paramType.getActualTypeArguments();
                if (argTypes.length > 0) {
                    return (Class<?>) argTypes[0];
                }
            }
            return null;
        }


    }

}
