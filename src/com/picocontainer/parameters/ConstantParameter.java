/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.picocontainer.parameters;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.picocontainer.ComponentAdapter;
import com.picocontainer.NameBinding;
import com.picocontainer.Parameter;
import com.picocontainer.exceptions.PicoCompositionException;
import com.picocontainer.PicoContainer;
import com.picocontainer.exceptions.PicoException;
import com.picocontainer.PicoVisitor;


/**
 * A ConstantParameter should be used to pass in "constant" arguments to constructors. This
 * includes {@link String}s,{@link Integer}s or any other object that is not registered in
 * the container.
 *
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @author Thomas Heller
 */
@SuppressWarnings("serial")
public class ConstantParameter<T> extends AbstractParameter implements Parameter, Serializable {

    private final T value;


    public ConstantParameter(final T value) {
		this.value = value;

    }

    public Resolver resolve(final PicoContainer container, final ComponentAdapter<?> forAdapter,
                            final ComponentAdapter<?> injecteeAdapter, final Type expectedType, final NameBinding expectedNameBinding,
                            final boolean useNames, final Annotation binding) {
        if (expectedType instanceof Class) {
            return new Parameter.ValueResolver<>(isAssignable(expectedType), value, null);
        } else if (expectedType instanceof ParameterizedType) {
        	return new Parameter.ValueResolver<>(isAssignable(((ParameterizedType)expectedType).getRawType()), value, null);
        }
        return new Parameter.ValueResolver<>(true, value, null);
    }

    /**
     * {@inheritDoc}
     *
     * @see Parameter#verify(com.picocontainer.PicoContainer,com.picocontainer.ComponentAdapter,java.lang.reflect.Type,com.picocontainer.NameBinding,boolean,java.lang.annotation.Annotation)
     */
    public void verify(final PicoContainer container, final ComponentAdapter<?> adapter,
                       final Type expectedType, final NameBinding expectedNameBinding,
                       final boolean useNames, final Annotation binding) throws PicoException {
        if (!isAssignable(expectedType)) {
            throw new PicoCompositionException(
                expectedType + " is not assignable from " +
                        (value != null ? value.getClass().getName() : "null"));
        }
    }

    protected boolean isAssignable(final Type expectedType) {
        if (expectedType instanceof Class) {
            Class<?> expectedClass = (Class<?>) expectedType;
            if (checkPrimitive(expectedClass) || expectedClass.isInstance(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Visit the current {@link Parameter}.
     *
     * @see com.picocontainer.Parameter#accept(com.picocontainer.PicoVisitor)
     */
    public void accept(final PicoVisitor visitor) {
        visitor.visitParameter(this);
    }

    private boolean checkPrimitive(final Class<?> expectedType) {
        try {
            if (expectedType.isPrimitive()) {
                final Field field = value.getClass().getField("TYPE");
                final Class<?> type = (Class<?>) field.get(value);
                return expectedType.isAssignableFrom(type);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            //ignore
        }
        return false;
    }


	public Object getValue() {
		return value;
	}

}
