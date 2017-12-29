/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.picocontainer.behaviors;

import java.util.Properties;

import com.picocontainer.Characteristics;
import com.picocontainer.ComponentAdapter;
import com.picocontainer.ComponentMonitor;
import com.picocontainer.LifecycleStrategy;
import com.picocontainer.ObjectReference;
import com.picocontainer.exceptions.PicoCompositionException;
import com.picocontainer.parameters.ConstructorParameters;
import com.picocontainer.parameters.FieldParameters;
import com.picocontainer.parameters.MethodParameters;
import com.picocontainer.references.SimpleReference;

/**
 * factory class creating cached behaviours
 *
 * @author Aslak Helles&oslash;y
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @author Konstantin Pribluda
 */
@SuppressWarnings("serial")
public class Caching extends AbstractBehavior {

    @Override
    public <T> ComponentAdapter<T> createComponentAdapter(final ComponentMonitor monitor, final LifecycleStrategy lifecycle,
                                                          final Properties componentProps, final Object key, final Class<T> impl, final ConstructorParameters constructorParams, final FieldParameters[] fieldParams, final MethodParameters[] methodParams) throws PicoCompositionException {
        if (removePropertiesIfPresent(componentProps, Characteristics.NO_CACHE)) {
            return super.createComponentAdapter(monitor, lifecycle, componentProps, key, impl, constructorParams, fieldParams, methodParams);
        }
        removePropertiesIfPresent(componentProps, Characteristics.CACHE);
        return monitor.changedBehavior(new Cached<>(
                super.createComponentAdapter(monitor, lifecycle, componentProps, key, impl, constructorParams, fieldParams, methodParams), new SimpleReference<>())
        );
    }

    @Override
    public <T> ComponentAdapter<T> addComponentAdapter(final ComponentMonitor monitor, final LifecycleStrategy lifecycle,
                                                       final Properties componentProps, final ComponentAdapter<T> adapter) {
        if (removePropertiesIfPresent(componentProps, Characteristics.NO_CACHE)) {
            return super.addComponentAdapter(monitor, lifecycle, componentProps, adapter);
        }
        removePropertiesIfPresent(componentProps, Characteristics.CACHE);
        ComponentAdapter<T> delegate = super.addComponentAdapter(monitor, lifecycle, componentProps, adapter);
        return monitor.changedBehavior(new Cached<>(delegate, new SimpleReference<>()));
    }

    /**
     * <p>
     * {@link com.picocontainer.ComponentAdapter} implementation that caches the component instance.
     * </p>
     * <p>
     * This adapter supports components with a lifecycle, as it is a
     * {@link com.picocontainer.ChangedBehavior lifecycle manager} which will apply the delegate's
     * {@link com.picocontainer.LifecycleStrategy lifecycle strategy} to the cached
     * component instance. The lifecycle state is maintained so that the component
     * instance behaves in the expected way: it can't be started if already started,
     * it can't be started or stopped if disposed, it can't be stopped if not
     * started, it can't be disposed if already disposed.
     * </p>
     *
     * @author Mauro Talevi
     */
    @SuppressWarnings("serial")
    public static class Cached<T> extends Storing.Stored<T> {

        public Cached(final ComponentAdapter<T> delegate) {
            this(delegate, new SimpleReference<>());
        }

        public Cached(final ComponentAdapter<T> delegate, final ObjectReference<Instance<T>> instanceReference) {
            super(delegate, instanceReference);
        }

        @Override
        public String getDescriptor() {
            return "Cached" + getLifecycleDescriptor();
        }
    }
}
