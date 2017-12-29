/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.picocontainer.exceptions;

/**
 * Runtime Exception wrapper for a ClassNotFoundException.
 */
@SuppressWarnings("serial")
public class PicoClassNotFoundException extends PicoException {

    public PicoClassNotFoundException(final String className, final ClassNotFoundException cnfe) {
        super("Class '" + className + "' not found", cnfe);
    }
}
