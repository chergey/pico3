/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.picocontainer.injectors;

import static com.picocontainer.injectors.AnnotatedMethodInjection.getInjectionAnnotation;

import java.util.Properties;

import com.picocontainer.Characteristics;
import com.picocontainer.ComponentAdapter;
import com.picocontainer.ComponentMonitor;
import com.picocontainer.LifecycleStrategy;
import com.picocontainer.exceptions.PicoCompositionException;
import com.picocontainer.annotations.Inject;
import com.picocontainer.behaviors.AbstractBehavior;
import com.picocontainer.parameters.ConstructorParameters;
import com.picocontainer.parameters.FieldParameters;
import com.picocontainer.parameters.MethodParameters;

/** @author Paul Hammant */
@SuppressWarnings("serial")
public class MultiInjection extends AbstractInjectionType {
    private final String setterPrefix;

    public MultiInjection(final String setterPrefix) {
        this.setterPrefix = setterPrefix;
    }

    public MultiInjection() {
        this("set");
    }

    public <T> ComponentAdapter<T> createComponentAdapter(final ComponentMonitor monitor,
                                                          final LifecycleStrategy lifecycle,
                                                          final Properties componentProps,
                                                          final Object key,
                                                          final Class<T> impl,
                                                          final ConstructorParameters constructorParams, final FieldParameters[] fieldParams, final MethodParameters[] methodParams) throws PicoCompositionException {
        boolean useNames = AbstractBehavior.arePropertiesPresent(componentProps, Characteristics.USE_NAMES, true);
        boolean requireConsumptionOfAllParameters = !(AbstractBehavior.arePropertiesPresent(componentProps, Characteristics.ALLOW_UNUSED_PARAMETERS, false));

        return wrapLifeCycle(new MultiInjector<>(key, impl, monitor, setterPrefix, useNames, requireConsumptionOfAllParameters, constructorParams, fieldParams, methodParams), lifecycle);
    }

    /** @author Paul Hammant */
    @SuppressWarnings("serial")
    public static class MultiInjector<T> extends CompositeInjection.CompositeInjector<T> {

        @SuppressWarnings("unchecked")
		public MultiInjector(final Object key, final Class<T> impl, final ComponentMonitor monitor, final String setterPrefix, final boolean useNames, final boolean useAllParameter,
        		final ConstructorParameters constructorParams, final FieldParameters[] fieldParams, final MethodParameters[] methodParams) {
            super(key, impl, monitor, useNames,
                    monitor.newInjector(new ConstructorInjection.ConstructorInjector<T>(monitor, useNames, key, impl, constructorParams)),
                    monitor.newInjector(new SetterInjection.SetterInjector<>(key, impl, monitor, setterPrefix, useNames, "", false, methodParams)),
                    monitor.newInjector(new AnnotatedMethodInjection.AnnotatedMethodInjector<>(key, impl, methodParams, monitor, useNames, useAllParameter, Inject.class, getInjectionAnnotation("javax.inject.Inject"))),
                    monitor.newInjector(new AnnotatedFieldInjection.AnnotatedFieldInjector<>(key, impl, fieldParams, monitor, useNames, useAllParameter, Inject.class, getInjectionAnnotation("javax.inject.Inject")))
           );

        }

        @Override
		public String getDescriptor() {
            return "MultiInjector";
        }
    }
}
