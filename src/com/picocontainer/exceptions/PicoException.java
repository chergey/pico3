/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.picocontainer.exceptions;

/**
 * Superclass for all Exceptions in PicoContainer. You can use this if you want to catch all exceptions thrown by
 * PicoContainer. Be aware that some parts of the PicoContainer API will also throw {@link NullPointerException} when
 * <code>null</code> values are provided for method arguments, and this is not allowed.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 */
@SuppressWarnings("serial")
public abstract class PicoException extends RuntimeException {

    /**
     * Construct a new exception with no cause and no detail message. Note modern JVMs may still track the exception
     * that caused this one.
     */
    protected PicoException() {
    }

    /**
     * Construct a new exception with no cause and the specified detail message.  Note modern JVMs may still track the
     * exception that caused this one.
     *
     * @param message the message detailing the exception.
     */
    protected PicoException(final String message) {
        super(message);
    }

    /**
     * Construct a new exception with the specified cause and no detail message.
     *
     * @param cause the exception that caused this one.
     */
    protected PicoException(final Throwable cause) {
        super(cause);
    }

    /**
     * Construct a new exception with the specified cause and the specified detail message.
     *
     * @param message the message detailing the exception.
     * @param cause   the exception that caused this one.
     */
    protected PicoException(final String message, final Throwable cause) {
        super(message,cause);
    }

}
