/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader.model;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.lang3.Validate;

import ch.sigi.tolerantreader.annotation.CustomName;
import ch.sigi.tolerantreader.annotation.CustomPath;

public class Node implements Serializable {

    private final Class type;
    private final Class genericType;
    private final CustomPath customPath;
    private final CustomName customName;

    /**
     * @param type not <code>null</code>
     * @param genericType nullable
     * @param customPath nullable
     * @param customName nullable
     */
    private Node(Class type, Class genericType, CustomPath customPath, CustomName customName) {
        this.type = Validate.notNull(type);
        this.genericType = genericType;
        this.customPath = customPath;
        this.customName = customName;
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

    /**
     * ******************************************************************************************************
     * The Builder
     * ******************************************************************************************************
     */
    public static final class Builder {

        private Class type;
        private Class genericType;
        private CustomPath customPath;
        private CustomName customName;

        /**
         * @param field not <code>null</code>
         */
        public static Builder forField(Field field) {
            Builder builder = new Builder();
            builder.type = Validate.notNull(field.getType());
            builder.genericType = getGenericTypeClass(field);
            builder.customPath = getAnnotationOfType(field, CustomPath.class);
            builder.customName = getAnnotationOfType(field, CustomName.class);

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
         */
        public static Builder forType(Class type) {
            Builder builder = new Builder();
            builder.type = Validate.notNull(type);
            return builder;
        }

        public Node build() {
            return new Node(
                    type,
                    genericType,
                    customPath,
                    customName);
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
