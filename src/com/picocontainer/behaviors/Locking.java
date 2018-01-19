/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.picocontainer.behaviors;

import java.lang.reflect.Type;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.picocontainer.Characteristics;
import com.picocontainer.ComponentAdapter;
import com.picocontainer.ComponentMonitor;
import com.picocontainer.LifecycleStrategy;
import com.picocontainer.exceptions.PicoCompositionException;
import com.picocontainer.PicoContainer;
import com.picocontainer.parameters.ConstructorParameters;
import com.picocontainer.parameters.FieldParameters;
import com.picocontainer.parameters.MethodParameters;

/**
 * This behavior factory provides java.util.concurrent locks.  It is recommended to be used instead
 * of {@link com.picocontainer.behaviors.Synchronizing} since it results in better performance.
 *
 * @author Aslak Helles&oslash;y
 * @author Paul Hammant.
 */
@SuppressWarnings("serial")
public class Locking extends AbstractBehavior {

    /**
     * {@inheritDoc}
     **/
    @Override
    public <T> ComponentAdapter<T> createComponentAdapter(final ComponentMonitor monitor, final LifecycleStrategy lifecycle,
                                                          final Properties componentProps, final Object key, final Class<T> impl, final ConstructorParameters constructorParams, final FieldParameters[] fieldParams, final MethodParameters[] methodParams) {

        if (removePropertiesIfPresent(componentProps, Characteristics.NO_LOCK)) {
            return super.createComponentAdapter(monitor, lifecycle, componentProps, key, impl, constructorParams, fieldParams, methodParams);
        }

        removePropertiesIfPresent(componentProps, Characteristics.LOCK);
        return monitor.changedBehavior(new Locked<T>(super.createComponentAdapter(monitor, lifecycle, componentProps, key, impl, constructorParams, fieldParams, methodParams)));
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public <T> ComponentAdapter<T> addComponentAdapter(final ComponentMonitor monitor, final LifecycleStrategy lifecycle,
                                                       final Properties componentProps, final ComponentAdapter<T> adapter) {
        if (removePropertiesIfPresent(componentProps, Characteristics.NO_LOCK)) {
            return super.addComponentAdapter(monitor, lifecycle, componentProps, adapter);
        }

        removePropertiesIfPresent(componentProps, Characteristics.LOCK);
        return monitor.changedBehavior(new Locked<T>(super.addComponentAdapter(monitor, lifecycle, componentProps, adapter)));
    }

    /**
     * @author Paul Hammant
     */
    @SuppressWarnings("serial")
    public static class Locked<T> extends AbstractChangedBehavior<T> {

        /**
         * Reentrant lock.
         */
        private final Lock lock = new ReentrantLock();

        public Locked(final ComponentAdapter<T> delegate) {
            super(delegate);
        }

        @Override
        public T getComponentInstance(final PicoContainer container, final Type into) throws PicoCompositionException {
            T retVal = null;
            lock.lock();
            try {
                retVal = super.getComponentInstance(container, into);
            } finally {
                lock.unlock();
            }
            return retVal;
        }

        public String getDescriptor() {
            return "Locked";
        }

    }
}