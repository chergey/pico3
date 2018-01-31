/*****************************************************************************
 * Copyright (C) 2003-2011 PicoContainer Committers. All rights reserved.    *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package com.picocontainer.lifecycle;

import com.picocontainer.ComponentAdapter;
import com.picocontainer.LifecycleStrategy;

/**
 * Allow for use of alternate LifecycleStrategy strategies to be used
 * at the same time. A component can be started/stopped/disposed according
 * to *any* of the supplied LifecycleStrategy instances.
 *
 * @author Paul Hammant
 */
public class CompositeLifecycleStrategy<T> implements LifecycleStrategy<T> {

    private final LifecycleStrategy<T>[] alternateStrategies;

    public CompositeLifecycleStrategy(final LifecycleStrategy... alternateStrategies) {
        this.alternateStrategies = alternateStrategies;
    }

    public void start(final T component) {
        for (LifecycleStrategy<T> lifecycle : alternateStrategies) {
            lifecycle.start(component);
        }
    }

    public void stop(final T component) {
        for (LifecycleStrategy<T> lifecycle : alternateStrategies) {
            lifecycle.stop(component);
        }
    }

    public void dispose(final T component) {
        for (LifecycleStrategy<T> lifecycle : alternateStrategies) {
            lifecycle.dispose(component);
        }
    }

    public boolean hasLifecycle(final Class<T> type) {
        for (LifecycleStrategy<T> lifecycle : alternateStrategies) {
            if (lifecycle.hasLifecycle(type)) {
                return true;
            }
        }
        return false;
    }

    public boolean calledAfterContextStart(final ComponentAdapter<T> adapter) {
        for (LifecycleStrategy<T> lifecycle : alternateStrategies) {
            if (lifecycle.calledAfterContextStart(adapter)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean calledAfterConstruction(ComponentAdapter<T> adapter) {
        for (LifecycleStrategy<T> lifecycle : alternateStrategies) {
            if (lifecycle.calledAfterConstruction(adapter)) {
                return true;
            }
        }
        return false;
    }
}
