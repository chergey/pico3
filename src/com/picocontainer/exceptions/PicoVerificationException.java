/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.picocontainer.exceptions;

import com.picocontainer.PicoContainer;

import java.util.ArrayList;
import java.util.List;


/**
 * Subclass of {@link PicoException} that is thrown when a {@link PicoContainer} hierarchy
 * cannot be verified. A failing verification is caused by ambuigities or missing dependencies
 * between the registered components and their parameters. This exception is designed as a
 * collector for all Exceptions occurring at the verification of the complete container
 * hierarchy. The verification is normally done with the
 * {@link com.picocontainer.visitors.VerifyingVisitor}, that will throw this exception.
 */
@SuppressWarnings("serial")
public class PicoVerificationException extends PicoException {

	/**
     * The exceptions that caused this one.
     */
    private final List<Throwable> nestedExceptions = new ArrayList<Throwable>();

    /**
     * Construct a new exception with a list of exceptions that caused this one.
     *
     * @param nestedExceptions the exceptions that caused this one.
     */
    public PicoVerificationException(final List<? extends Throwable> nestedExceptions) {
        this.nestedExceptions.addAll(nestedExceptions);
    }

    /**
     * Retrieve the list of exceptions that caused this one.
     *
     * @return the list of exceptions that caused this one.
     */
    public List<Throwable> getNestedExceptions() {
        return nestedExceptions;
    }

    /**
     * Return a string listing of all the messages associated with the exceptions that caused
     * this one.
     *
     * @return a string listing of all the messages associated with the exceptions that caused
     *               this one.
     */
    @Override
	public String getMessage() {
        return nestedExceptions.toString();
    }
}
