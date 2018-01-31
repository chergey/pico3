/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.picocontainer.lifecycle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.picocontainer.ComponentAdapter;
import com.picocontainer.ComponentMonitor;
import com.picocontainer.Disposable;
import com.picocontainer.exceptions.PicoLifecycleException;
import com.picocontainer.Startable;

/**
 * Startable lifecycle strategy.  Starts and stops component if Startable,
 * and disposes it if Disposable.
 * <p>
 * A subclass of this class can define other intrfaces for Startable/Disposable as well as other method names
 * for start/stop/dispose
 *
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 * @see Startable
 * @see Disposable
 */
@SuppressWarnings("serial")
public class StartableLifecycleStrategy<T> extends AbstractMonitoringLifecycleStrategy<T> {


    private transient Method start, stop, dispose;

    public StartableLifecycleStrategy(final ComponentMonitor monitor) {
        super(monitor);
    }

    @SuppressWarnings("unchecked")
    private void doMethodsIfNotDone() {
        try {
            if (start == null) {
                start = getStartableInterface().getMethod(getStartMethodName());
            }
            if (stop == null) {
                stop = getStartableInterface().getMethod(getStopMethodName());
            }
            if (dispose == null) {
                dispose = getDisposableInterface().getMethod(getDisposeMethodName());
            }
        } catch (NoSuchMethodException ignored) {
        }
    }

    /**
     * Retrieve the lifecycle method name that represents the dispose method.
     *
     * @return the dispose method name. ('dispose')
     */
    protected String getDisposeMethodName() {
        return "dispose";
    }

    /**
     * Retrieve the lifecycle method name that represents the stop method.
     *
     * @return the stop method name ('stop')
     */
    protected String getStopMethodName() {
        return "stop";
    }

    /**
     * Retrieve the lifecycle method name that represents the start method.
     *
     * @return the stop method name ('start')
     */
    protected String getStartMethodName() {
        return "start";
    }


    /**
     * {@inheritDoc}
     **/
    @SuppressWarnings("unchecked")
    public void start(final T component) {
        doMethodsIfNotDone();
        if (component != null && getStartableInterface().isAssignableFrom(component.getClass())) {
            long str = System.currentTimeMillis();
            currentMonitor().invoking(null, null, start, component, new Object[0]);
            try {
                startComponent(component);
                currentMonitor().invoked(null, null, start, component, System.currentTimeMillis() - str, null, new Object[0]);
            } catch (RuntimeException cause) {
                currentMonitor().lifecycleInvocationFailed(null, null, start, component, cause); // may re-throw
            }
        }
    }

    protected void startComponent(final T component) {
        doLifecycleMethod(component, start);
    }

    private void doLifecycleMethod(final T component, final Method lifecycleMethod) {
        try {
            lifecycleMethod.invoke(component);
        } catch (IllegalAccessException e) {
            throw new PicoLifecycleException(lifecycleMethod, component, e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            }
            throw new PicoLifecycleException(lifecycleMethod, component, e.getTargetException());
        }
    }

    protected void stopComponent(final T component) {
        doLifecycleMethod(component, stop);
    }

    protected void disposeComponent(final T component) {
        doLifecycleMethod(component, dispose);
    }

    /**
     * {@inheritDoc}
     **/
    @SuppressWarnings("unchecked")
    public void stop(final T component) {
        doMethodsIfNotDone();
        if (component != null && getStartableInterface().isAssignableFrom(component.getClass())) {
            long str = System.currentTimeMillis();
            currentMonitor().invoking(null, null, stop, component, new Object[0]);
            try {
                stopComponent(component);
                currentMonitor().invoked(null, null, stop, component, System.currentTimeMillis() - str, null, new Object[0]);
            } catch (RuntimeException cause) {
                currentMonitor().lifecycleInvocationFailed(null, null, stop, component, cause); // may re-throw
            }
        }
    }

    /**
     * {@inheritDoc}
     **/
    @SuppressWarnings("unchecked")
    public void dispose(final T component) {
        doMethodsIfNotDone();
        if (component != null && getDisposableInterface().isAssignableFrom(component.getClass())) {
            long str = System.currentTimeMillis();
            currentMonitor().invoking(null, null, dispose, component, new Object[0]);
            try {
                disposeComponent(component);
                currentMonitor().invoked(null, null, dispose, component, System.currentTimeMillis() - str, null, new Object[0]);
            } catch (RuntimeException cause) {
                currentMonitor().lifecycleInvocationFailed(null, null, dispose, component, cause); // may re-throw
            }
        }
    }

    /**
     * {@inheritDoc}
     **/
    @SuppressWarnings("unchecked")
    public boolean hasLifecycle(final Class<T> type) {
        return getStartableInterface().isAssignableFrom(type) || getDisposableInterface().isAssignableFrom(type);
    }

    @Override
    public boolean calledAfterConstruction(ComponentAdapter<T> adapter) {
        return false;
    }

    protected Class<?> getDisposableInterface() {
        return Disposable.class;
    }

    protected Class<?> getStartableInterface() {
        return Startable.class;
    }
}
