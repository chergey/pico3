/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.picocontainer.behaviors;

import java.lang.reflect.Type;
import java.util.Properties;

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
 * factory class creating guard behaviour
 *
 * @author Paul Hammant
 */
@SuppressWarnings("serial")
public class Guarding extends AbstractBehavior {

    @Override
	public <T> ComponentAdapter<T> createComponentAdapter(final ComponentMonitor monitor, final LifecycleStrategy lifecycle,
            final Properties componentProps, final Object key, final Class<T> impl, final ConstructorParameters constructorParams, final FieldParameters[] fieldParams, final MethodParameters[] methodParams) throws PicoCompositionException {
        String guard = getAndRemovePropertiesIfPresentByKey(componentProps, Characteristics.GUARD);
        ComponentAdapter<T> delegate = super.createComponentAdapter(monitor, lifecycle,
                componentProps, key, impl, constructorParams, fieldParams, methodParams);
        if (guard == null) {
            return delegate;
        } else {
            return monitor.changedBehavior(new Guarded<T>(delegate, guard));
        }

    }

    @Override
	public <T> ComponentAdapter<T> addComponentAdapter(final ComponentMonitor monitor, final LifecycleStrategy lifecycle,
            final Properties componentProps, final ComponentAdapter<T> adapter) {
        String guard = getAndRemovePropertiesIfPresentByKey(componentProps, Characteristics.GUARD);
        ComponentAdapter<T> delegate = super.addComponentAdapter(monitor, lifecycle, componentProps, adapter);
        if (guard == null) {
            return delegate;
        } else {
            return monitor.changedBehavior(monitor.changedBehavior(new Guarded<T>(delegate, guard)));
        }
    }

    /**
     * behaviour for allows components to be guarded by another component
     *
     * @author Paul Hammant
     * @param <T>
     */
    @SuppressWarnings("serial")
    public static class Guarded<T> extends AbstractChangedBehavior<T> {
        private final String guard;

        public Guarded(final ComponentAdapter<T> delegate, final String guard) {
            super(delegate);
            this.guard = guard;
        }

        @Override
		public T getComponentInstance(final PicoContainer container, final Type into) throws PicoCompositionException {
            container.getComponentInto(guard, into);
            return super.getComponentInstance(container, into);
        }

        public String getDescriptor() {
            return "Guarded(with " + guard + ")";
        }


    }
}