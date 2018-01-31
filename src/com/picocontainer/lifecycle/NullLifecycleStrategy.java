/*****************************************************************************
 * Copyright (C) 2003-2011 PicoContainer Committers. All rights reserved.    *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package com.picocontainer.lifecycle;

import java.io.Serializable;

import com.picocontainer.ComponentAdapter;
import com.picocontainer.LifecycleStrategy;

/**
 * Lifecycle strategy that does nothing.
 *
 */
@SuppressWarnings("serial")
public class NullLifecycleStrategy<T> implements LifecycleStrategy<T>, Serializable {


    /** {@inheritDoc} **/
	public void start(final T component) {
		//Does nothing
    }

    /** {@inheritDoc} **/
    public void stop(final T component) {
		//Does nothing
    }

    /** {@inheritDoc} **/
    public void dispose(final T component) {
		//Does nothing
    }

    /** {@inheritDoc} **/
    public boolean hasLifecycle(final Class<T> type) {
        return false;
    }

    public boolean calledAfterContextStart(final ComponentAdapter<T> adapter) {
        return false;
    }

    @Override
    public boolean calledAfterConstruction(ComponentAdapter<T> adapter) {
        return false;
    }
}
