/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.picocontainer.behaviors;

import java.lang.reflect.Field;
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
 * Behavior for Field Decorating. This factory will create {@link com.picocontainer.behaviors.FieldDecorating.FieldDecorated} that will
 * allow you to decorate fields on the component instance that has been created
 *
 * @author Paul Hammant
 */
public abstract class FieldDecorating extends AbstractBehavior implements Decorator {
    private final Class<?> fieldClass;

    public FieldDecorating(final Class<?> fieldClass) {
        this.fieldClass = fieldClass;
    }

    @Override
    public <T> ComponentAdapter<T> createComponentAdapter(
            final ComponentMonitor monitor, final LifecycleStrategy lifecycle, final Properties componentProps, final Object key, final Class<T> impl, final ConstructorParameters constructorParams, final FieldParameters[] fieldParams, final MethodParameters[] methodParams)
            throws PicoCompositionException {
        return monitor.changedBehavior(new FieldDecorated<T>(
                super.createComponentAdapter(monitor, lifecycle, componentProps,
                        key, impl, constructorParams, fieldParams, methodParams),
                fieldClass, this));
    }

    @SuppressWarnings("serial")
    public static class FieldDecorated<T> extends AbstractChangedBehavior<T> {
        private final Class<?> fieldClass;
        private final Decorator decorator;

        public FieldDecorated(final ComponentAdapter<T> delegate, final Class<?> fieldClass, final Decorator decorator) {
            super(delegate);
            this.fieldClass = fieldClass;
            this.decorator = decorator;
        }

        @Override
        public T getComponentInstance(final PicoContainer container, final Type into)
                throws PicoCompositionException {
            T instance = super.getComponentInstance(container, into);
            Field[] fields = instance.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getType() == fieldClass) {
                    Object value = decorator.decorate(instance);
                    field.setAccessible(true);
                    try {
                        field.set(instance, value);
                    } catch (IllegalAccessException e) {
                        throw new PicoCompositionException(e);
                    }
                }
            }
            return instance;
        }


        public String getDescriptor() {
            return "FieldDecorated";
        }
    }
}