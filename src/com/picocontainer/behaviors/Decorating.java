/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.picocontainer.behaviors;

import java.lang.reflect.Type;
import java.util.Properties;

import com.picocontainer.ComponentAdapter;
import com.picocontainer.ComponentMonitor;
import com.picocontainer.Decorator;
import com.picocontainer.LifecycleStrategy;
import com.picocontainer.exceptions.PicoCompositionException;
import com.picocontainer.PicoContainer;
import com.picocontainer.parameters.ConstructorParameters;
import com.picocontainer.parameters.FieldParameters;
import com.picocontainer.parameters.MethodParameters;

/**
 * Behavior for Decorating. This factory will create {@link com.picocontainer.behaviors.Decorating.Decorated} that will
 * allow you to decorate what you like on the component instance that has been created
 *
 * @author Paul Hammant
 */
public abstract class Decorating extends AbstractBehavior implements Decorator {

    @Override
	public <T> ComponentAdapter<T> createComponentAdapter(final ComponentMonitor monitor, final LifecycleStrategy lifecycle,
                                                   final Properties componentProps, final Object key,
                                                   final Class<T> impl, final ConstructorParameters constructorParams, final FieldParameters[] fieldParams, final MethodParameters[] methodParams) throws PicoCompositionException {
        return monitor.changedBehavior(new Decorated<T>(super.createComponentAdapter(monitor, lifecycle,
                componentProps,key, impl, constructorParams, fieldParams, methodParams), this));
    }

    @SuppressWarnings("serial")
    public static class Decorated<T> extends AbstractChangedBehavior<T> {
        private final Decorator decorator;

        public Decorated(final ComponentAdapter<T> delegate, final Decorator decorator) {
            super(delegate);
            this.decorator = decorator;
        }

        @Override
		public T getComponentInstance(final PicoContainer container, final Type into)
                throws PicoCompositionException {
            T instance = super.getComponentInstance(container, into);
            decorator.decorate(instance);
            return instance;
        }

        public String getDescriptor() {
            return "Decorated";
        }

    }

}