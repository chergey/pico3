/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.picocontainer.lifecycle;

import com.picocontainer.exceptions.PicoException;

/**
 * Subclass of {@link PicoException} that is thrown when there is a problem
 * invoking lifecycle methods via reflection.
 *
 * @author Paul Hammant
 * @author Mauro Talevi
 */
@SuppressWarnings("serial")
public class ReflectionLifecycleException extends PicoException {


	/**
     * Construct a new exception with the specified cause and the specified detail message.
     *
     * @param message the message detailing the exception.
     * @param cause   the exception that caused this one.
     */
    protected ReflectionLifecycleException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
