/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.picocontainer.injectors;

import java.lang.reflect.Type;
import java.util.Properties;

import com.picocontainer.Characteristics;
import com.picocontainer.ComponentAdapter;
import com.picocontainer.ComponentMonitor;
import com.picocontainer.InjectionType;
import com.picocontainer.LifecycleStrategy;
import com.picocontainer.exceptions.PicoCompositionException;
import com.picocontainer.PicoContainer;
import com.picocontainer.behaviors.AbstractBehavior;
import com.picocontainer.parameters.ConstructorParameters;
import com.picocontainer.parameters.FieldParameters;
import com.picocontainer.parameters.MethodParameters;

@SuppressWarnings("serial")
public class Reinjection extends CompositeInjection {

    public Reinjection(final InjectionType reinjectionType, final PicoContainer parent) {
        super(new ReinjectionInjectionType(parent), reinjectionType);
    }

	private static class ReinjectionInjector<T> extends AbstractInjector<T> {
        private final PicoContainer parent;

        public ReinjectionInjector(final Object key, final Class<T> impl, final ComponentMonitor monitor, final PicoContainer parent,
				final boolean useNames, final ConstructorParameters constructorParams, final FieldParameters[] fieldParams,
				final MethodParameters[] methodParams) {
            super(key, impl, monitor, useNames, methodParams);
            this.parent = parent;
		}

		@Override
		public T getComponentInstance(final PicoContainer container, final Type into) throws PicoCompositionException {
            return (T) parent.getComponentInto(getComponentKey(), into);
        }
    }

	private static class ReinjectionInjectionType extends AbstractInjectionType {
        private final PicoContainer parent;

        public ReinjectionInjectionType(final PicoContainer parent) {
            this.parent = parent;
        }

        public <T> ComponentAdapter<T> createComponentAdapter(final ComponentMonitor monitor, final LifecycleStrategy lifecycle,
                final Properties componentProps, final Object key, final Class<T> impl, final ConstructorParameters constructorParams, final FieldParameters[] fieldParams, final MethodParameters[] methodParams) throws PicoCompositionException {
            boolean useNames = AbstractBehavior.arePropertiesPresent(componentProps, Characteristics.USE_NAMES, true);
            return new ReinjectionInjector<T>(key, impl, monitor, parent, useNames, constructorParams, fieldParams, methodParams);
        }
    }
}
