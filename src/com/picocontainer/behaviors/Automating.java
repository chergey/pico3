/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.picocontainer.behaviors;

import java.io.Serializable;
import java.util.Properties;

import com.picocontainer.ChangedBehavior;
import com.picocontainer.Characteristics;
import com.picocontainer.ComponentAdapter;
import com.picocontainer.ComponentMonitor;
import com.picocontainer.LifecycleStrategy;
import com.picocontainer.exceptions.PicoCompositionException;
import com.picocontainer.parameters.ConstructorParameters;
import com.picocontainer.parameters.FieldParameters;
import com.picocontainer.parameters.MethodParameters;

@SuppressWarnings("serial")
public class Automating extends AbstractBehavior implements Serializable {


    @Override
    public <T> ComponentAdapter<T> createComponentAdapter(final ComponentMonitor monitor,
                                                          final LifecycleStrategy lifecycle,
                                                          final Properties componentProps,
                                                          final Object key,
                                                          final Class<T> impl,
                                                          final ConstructorParameters constructorParams, final FieldParameters[] fieldParams, final MethodParameters[] methodParams) throws PicoCompositionException {
        removePropertiesIfPresent(componentProps, Characteristics.AUTOMATIC);
        return monitor.changedBehavior(new Automated<T>(super.createComponentAdapter(monitor,
                lifecycle,
                componentProps,
                key,
                impl,
                constructorParams, fieldParams, methodParams)));
    }

    @Override
    public <T> ComponentAdapter<T> addComponentAdapter(final ComponentMonitor monitor,
                                                       final LifecycleStrategy lifecycle,
                                                       final Properties componentProps,
                                                       final ComponentAdapter<T> adapter) {
        removePropertiesIfPresent(componentProps, Characteristics.AUTOMATIC);
        return monitor.changedBehavior(new Automated<T>(super.addComponentAdapter(monitor,
                lifecycle,
                componentProps,
                adapter)));
    }

    @SuppressWarnings("serial")
    public static class Automated<T> extends AbstractChangedBehavior<T> implements ChangedBehavior<T>, Serializable {


        public Automated(final ComponentAdapter<T> delegate) {
            super(delegate);
        }

        @Override
        public boolean hasLifecycle(final Class<T> type) {
            return true;
        }



        public String getDescriptor() {
            return "Automated";
        }
    }
}
