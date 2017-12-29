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
import com.picocontainer.exceptions.PicoCompositionException;
import com.picocontainer.parameters.ConstructorParameters;
import com.picocontainer.parameters.FieldParameters;
import com.picocontainer.parameters.MethodParameters;
import com.picocontainer.references.ThreadLocalReference;

/** @author Paul Hammant */
@SuppressWarnings("serial")
public class ThreadCaching extends AbstractBehavior {

    @Override
	public <T> ComponentAdapter<T> createComponentAdapter(final ComponentMonitor monitor, final LifecycleStrategy lifecycle,
                        final Properties componentProps, final Object key, final Class<T> impl, final ConstructorParameters constructorParams, final FieldParameters[] fieldParams, final MethodParameters[] methodParams) throws PicoCompositionException {
        if (removePropertiesIfPresent(componentProps, Characteristics.NO_CACHE)) {
            return super.createComponentAdapter(monitor, lifecycle, componentProps, key, impl, constructorParams, fieldParams, methodParams);
        }
        removePropertiesIfPresent(componentProps, Characteristics.CACHE);
        return monitor.changedBehavior(new ThreadCached<>(
                super.createComponentAdapter(monitor, lifecycle, componentProps, key, impl, constructorParams, fieldParams, methodParams)));

    }

    @Override
	public <T> ComponentAdapter<T> addComponentAdapter(final ComponentMonitor monitor, final LifecycleStrategy lifecycle,
                                                       final Properties componentProps, final ComponentAdapter<T> adapter) {
        if (removePropertiesIfPresent(componentProps, Characteristics.NO_CACHE)) {
            return super.addComponentAdapter(monitor, lifecycle, componentProps, adapter);
        }
        removePropertiesIfPresent(componentProps, Characteristics.CACHE);
        return monitor.changedBehavior(new ThreadCached<>(super.addComponentAdapter(monitor, lifecycle, componentProps, adapter)));
    }

    /**
     * <p>
     * This behavior supports caches values per thread.
     * </p>
     *
     * @author Paul Hammant
     */
    public static final class ThreadCached<T> extends Storing.Stored<T> {

        public ThreadCached(final ComponentAdapter<T> delegate) {
            super(delegate, new ThreadLocalReference<>());
        }

        @Override
		public String getDescriptor() {
            return "ThreadCached" + getLifecycleDescriptor();
        }
    }
}