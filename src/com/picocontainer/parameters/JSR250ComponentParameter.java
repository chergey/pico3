/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.picocontainer.parameters;

import com.picocontainer.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;


public class JSR250ComponentParameter extends ComponentParameter {

    private final String name;

    private final Class<?> clazz;

    private Object object;
    private ResourceRegistry resourceRegistry;

    public JSR250ComponentParameter(final String name, final Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getName() {
        return name;
    }

    @Override
    public Resolver resolve(PicoContainer container, ComponentAdapter<?> forAdapter, ComponentAdapter<?> injecteeAdapter, Type expectedType, NameBinding expectedNameBinding, boolean useNames, Annotation binding) {

        if (resourceRegistry == null) {
            resourceRegistry = container.getComponent(ResourceRegistry.class);
        }
        if (object == null) {
            object = resourceRegistry.get(name, clazz);
        }

        return new Resolver() {
            @Override
            public boolean isResolved() {
                return object != null;
            }

            @Override
            public Object resolveInstance(Type into) {
                return object;
            }

            @Override
            public ComponentAdapter<?> getComponentAdapter() {
                return null;
            }
        };
    }
}
