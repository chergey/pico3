/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.picocontainer.parameters;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.picocontainer.ComponentAdapter;
import com.picocontainer.NameBinding;
import com.picocontainer.exceptions.PicoCompositionException;
import com.picocontainer.PicoContainer;
import com.picocontainer.PicoVisitor;

/**
 * Once in a great while, you actually want to pass in 'null' as an argument.  Instead
 * of bypassing the type checking mechanisms available in
 * {@link com.picocontainer.parameters.ConstantParameter ConstantParameter}, we provide a  <em>Special Type</em>
 * geared to marking nulls.
 * @author Michael Rimov
 *
 */
@SuppressWarnings("serial")
public class NullParameter extends AbstractParameter implements Serializable {

	/**
	 * The one and only instance of null parameter.
	 */
	public static final NullParameter INSTANCE = new NullParameter();

	/**
	 * Only once instance of Null parameter needed.
	 */
	protected NullParameter() {
	}

	/**
	 * {@inheritDoc}
	 * @see com.picocontainer.Parameter#accept(com.picocontainer.PicoVisitor)
	 */
	public void accept(final PicoVisitor visitor) {
		visitor.visitParameter(this);
	}

	/**
	 * {@inheritDoc}
	 * @see com.picocontainer.Parameter#resolve(com.picocontainer.PicoContainer, com.picocontainer.ComponentAdapter, com.picocontainer.ComponentAdapter, java.lang.reflect.Type, com.picocontainer.NameBinding, boolean, java.lang.annotation.Annotation)
	 */
	public Resolver resolve(final PicoContainer container,
			final ComponentAdapter<?> forAdapter,
			final ComponentAdapter<?> injecteeAdapter, final Type expectedType,
			final NameBinding expectedNameBinding, final boolean useNames,
			final Annotation binding) {
		return new ValueResolver(isAssignable(expectedType), null, null);
	}

	/**
	 * {@inheritDoc}
	 * @see com.picocontainer.Parameter#verify(com.picocontainer.PicoContainer, com.picocontainer.ComponentAdapter, java.lang.reflect.Type, com.picocontainer.NameBinding, boolean, java.lang.annotation.Annotation)
	 */
	public void verify(final PicoContainer container, final ComponentAdapter<?> adapter,
			final Type expectedType, final NameBinding expectedNameBinding,
			final boolean useNames, final Annotation binding) {
		if (!isAssignable(expectedType)) {
			throw new PicoCompositionException(expectedType + " cannot be assigned a null value");
		}
	}

	/**
	 * Nulls cannot be assigned to primitives.
	 * @param expectedType
	 * @return
	 */
    protected boolean isAssignable(final Type expectedType) {
        if (expectedType instanceof Class<?>) {
            Class<?> expectedClass = Class.class.cast(expectedType);
			return !expectedClass.isPrimitive();
        }
        return true;
    }

	@Override
	public String getTargetName() {
		return null;
	}


}
