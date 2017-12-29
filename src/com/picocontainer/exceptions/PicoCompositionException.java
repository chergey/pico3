/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.picocontainer.exceptions;

/**
 * Subclass of {@link PicoException} that is thrown when there is:
 *   - a problem initializing the container
 *   - a cyclic dependency between components occurs.
 *   - problem adding a component
 *   - a request for a component that is ambiguous.
 *
 */
@SuppressWarnings("serial")
public class PicoCompositionException extends PicoException {
    /**
     * Construct a new exception with no cause and the specified detail message.  Note modern JVMs may still track the
     * exception that caused this one.
     *
     * @param message the message detailing the exception.
     */
    public PicoCompositionException(final String message) {
        super(message);
    }

    /**
     * Construct a new exception with the specified cause and no detail message.
     *
     * @param cause the exception that caused this one.
     */
    public PicoCompositionException(final Throwable cause) {
        super(cause);
    }

    /**
     * Construct a new exception with the specified cause and the specified detail message.
     *
     * @param message the message detailing the exception.
     * @param cause   the exception that caused this one.
     */
    public PicoCompositionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
