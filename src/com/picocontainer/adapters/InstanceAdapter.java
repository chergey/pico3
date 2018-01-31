/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.picocontainer.adapters;

import java.lang.reflect.Type;

import com.picocontainer.ComponentAdapter;
import com.picocontainer.ComponentLifecycle;
import com.picocontainer.ComponentMonitor;
import com.picocontainer.LifecycleStrategy;
import com.picocontainer.exceptions.PicoCompositionException;
import com.picocontainer.PicoContainer;
import com.picocontainer.lifecycle.NullLifecycleStrategy;
import com.picocontainer.monitors.NullComponentMonitor;

/**
 * <p>
 * Component adapter which wraps a component instance.
 * </p>
 * <p>
 * This component adapter supports both a {@link com.picocontainer.ChangedBehavior Behavior} and a
 * {@link com.picocontainer.LifecycleStrategy LifecycleStrategy} to control the lifecycle of the component.
 * The lifecycle manager methods simply delegate to the lifecycle strategy methods
 * on the component instance.
 * </p>
 *
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant
 * @author Mauro Talevi
 */
@SuppressWarnings("serial")
public final class InstanceAdapter<T> extends AbstractAdapter<T> implements ComponentLifecycle<T>, LifecycleStrategy<T> {

    /**
     * The actual instance of the component.
     */
    private final T componentInstance;

    /**
     * Lifecycle Strategy for the component adpater.
     */
    private final LifecycleStrategy<T> lifecycle;
    private boolean started;


    public InstanceAdapter(final Object key, final T componentInstance, final LifecycleStrategy lifecycle, final ComponentMonitor monitor) throws PicoCompositionException {
        super(key, getInstanceClass(componentInstance), monitor);
        this.componentInstance = componentInstance;
        this.lifecycle = lifecycle;
    }

    public InstanceAdapter(final Object key, final T componentInstance) {
        this(key, componentInstance, new NullLifecycleStrategy(), new NullComponentMonitor());
    }

    public InstanceAdapter(final Object key, final T componentInstance, final LifecycleStrategy lifecycle) {
        this(key, componentInstance, lifecycle, new NullComponentMonitor());
    }

    public InstanceAdapter(final Object key, final T componentInstance, final ComponentMonitor monitor) {
        this(key, componentInstance, new NullLifecycleStrategy(), monitor);
    }

    private static Class getInstanceClass(final Object componentInstance) {
        if (componentInstance == null) {
            throw new NullPointerException("componentInstance cannot be null");
        }
        return componentInstance.getClass();
    }

    public T getComponentInstance(final PicoContainer container, final Type into) {
        return componentInstance;
    }

    public void verify(final PicoContainer container) {
    }

    public String getDescriptor() {
        return "Instance-";
    }

    public void start(final PicoContainer container) {
        start(componentInstance);
    }

    public void stop(final PicoContainer container) {
        stop(componentInstance);
    }

    public void dispose(final PicoContainer container) {
        dispose(componentInstance);
    }

    public boolean componentHasLifecycle() {
        return hasLifecycle((Class<T>) componentInstance.getClass());
    }

    public boolean isStarted() {
        return started;
    }

    // ~~~~~~~~ LifecycleStrategy ~~~~~~~~

    public void start(final T component) {
        lifecycle.start(componentInstance);
        started = true;
    }

    public void stop(final T component) {
        lifecycle.stop(componentInstance);
        started = false;
    }

    public void dispose(final T component) {
        lifecycle.dispose(componentInstance);
    }

    public boolean hasLifecycle(final Class<T> type) {
        return lifecycle.hasLifecycle(type);
    }

    public boolean calledAfterContextStart(final ComponentAdapter<T> adapter) {
        return lifecycle.calledAfterContextStart(adapter);
    }

    @Override
    public boolean calledAfterConstruction(ComponentAdapter<T> adapter) {
        return false;
    }
}
