/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.picocontainer.injectors;

import java.io.Serializable;
import java.lang.reflect.Type;

import com.picocontainer.ComponentAdapter;
import com.picocontainer.ComponentMonitor;
import com.picocontainer.ComponentMonitorStrategy;
import com.picocontainer.InjectionType;
import com.picocontainer.Injector;
import com.picocontainer.LifecycleStrategy;
import com.picocontainer.exceptions.PicoCompositionException;
import com.picocontainer.PicoContainer;
import com.picocontainer.PicoVisitor;
import com.picocontainer.lifecycle.NullLifecycleStrategy;
import com.picocontainer.monitors.NullComponentMonitor;

@SuppressWarnings("serial")
public abstract class AbstractInjectionType implements InjectionType, Serializable {

    public void verify(final PicoContainer container) {
    }

    public final void accept(final PicoVisitor visitor) {
        visitor.visitComponentFactory(this);
    }

    protected <T> ComponentAdapter<T> wrapLifeCycle(final Injector<T> injector, final LifecycleStrategy lifecycle) {
        if (lifecycle instanceof NullLifecycleStrategy) {
            return injector;
        } else {
            return new LifecycleAdapter<>(injector, lifecycle);
        }
    }


    public void dispose() {
    }

    private static class LifecycleAdapter<T> implements Injector<T>, LifecycleStrategy<T>, ComponentMonitorStrategy, Serializable {
        private final Injector<T> delegate;
        private final LifecycleStrategy<T> lifecycle;

        public LifecycleAdapter(final Injector<T> delegate, final LifecycleStrategy<T> lifecycle) {
            this.delegate = delegate;
            this.lifecycle = lifecycle;
        }

        public Object getComponentKey() {
            return delegate.getComponentKey();
        }

        public Class<? extends T> getComponentImplementation() {
            return delegate.getComponentImplementation();
        }

        public T getComponentInstance(final PicoContainer container, final Type into) throws PicoCompositionException {
            return delegate.getComponentInstance(container, into);
        }

        public void verify(final PicoContainer container) throws PicoCompositionException {
            delegate.verify(container);
        }

        public void accept(final PicoVisitor visitor) {
            delegate.accept(visitor);
        }

        public ComponentAdapter<T> getDelegate() {
            return delegate;
        }

        @SuppressWarnings("rawtypes")
        public <U extends ComponentAdapter> U findAdapterOfType(final Class<U> adapterType) {
            return delegate.findAdapterOfType(adapterType);
        }

        public String getDescriptor() {
            return "LifecycleAdapter";
        }

        @Override
        public String toString() {
            return getDescriptor() + ":" + delegate.toString();
        }

        public void start(final T component) {
            lifecycle.start(component);
        }

        public void stop(final T component) {
            lifecycle.stop(component);
        }

        public void dispose(final T component) {
            lifecycle.dispose(component);
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

        public ComponentMonitor changeMonitor(final ComponentMonitor monitor) {
            if (delegate instanceof ComponentMonitorStrategy) {
                return ((ComponentMonitorStrategy) delegate).changeMonitor(monitor);
            }

            return new NullComponentMonitor();
        }

        public ComponentMonitor currentMonitor() {
            if (delegate instanceof ComponentMonitorStrategy) {
                return ((ComponentMonitorStrategy) delegate).currentMonitor();
            }
            return null;
        }

        public Object decorateComponentInstance(final PicoContainer container, final Type into, final T instance) {
            return delegate.decorateComponentInstance(container, into, instance);
        }

        public Object partiallyDecorateComponentInstance(final PicoContainer container, final Type into, final T instance,
                                                         final Class<?> superclassPortion) {
            return delegate.partiallyDecorateComponentInstance(container, into, instance, superclassPortion);
        }
    }

}
