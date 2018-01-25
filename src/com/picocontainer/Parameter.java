/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.picocontainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.picocontainer.exceptions.PicoCompositionException;
import com.picocontainer.parameters.ComponentParameter;
import com.picocontainer.parameters.DefaultConstructorParameter;

/**
 * This class provides control over the arguments that will be passed to a constructor. It can be used for finer control over
 * what arguments are passed to a particular constructor.
 *
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @author Thomas Heller
 * @see MutablePicoContainer#addComponent(Object, Object, Parameter[]) a method on the
 * {@link MutablePicoContainer} interface which allows passing in of an array of {@linkplain Parameter Parameters}.
 * @see com.picocontainer.parameters.ComponentParameter an implementation of this interface that allows you to specify the key
 * used for resolving the parameter.
 * @see com.picocontainer.parameters.ConstantParameter an implementation of this interface that allows you to specify a constant
 * that will be used for resolving the parameter.
 */
public interface Parameter {

    /**
     * Zero parameter is used when you wish a component to be instantiated with its default constructor.  Ex:
     * <div class="source">
     * <pre>
     * 		MutablePicoContainer mpc = new PicoBuilder().build();
     * 		mpc.addComponent(Map.class, HashMap.class, Parameter.ZERO);
     * 		mpc.addComponent(List.class, ArrayList.class, Parameter.ZERO);
     * 	</pre>
     * </div>
     * <p>By specifying the default constructor in this example code, you allow PicoContainer to recognize
     * that HashMap(Collection) should <em>not</em> be used and avoid a CircularDependencyException.</p>
     */
    @Deprecated
    Parameter[] ZERO = new Parameter[]{DefaultConstructorParameter.INSTANCE};


    /**
     * Internally used to specify auto-wiring of the components.
     */
    Parameter[] DEFAULT = new Parameter[]{ComponentParameter.DEFAULT};

    Object NULL_RESULT = new Object();


    /**
     * Check if the Parameter can satisfy the expected type using the container.
     *
     * @param container           the container from which dependencies are resolved.
     * @param forAdapter          the {@link com.picocontainer.ComponentAdapter} that is asking for the instance
     * @param injecteeAdapter     the adapter to be injected into (null for N/A)
     * @param expectedType        the required type
     * @param expectedNameBinding Expected parameter name
     * @param useNames            should use parameter names for disambiguation
     * @param binding
     * @return <code>true</code> if the component parameter can be resolved.
     * @since 2.8.1
     */
    Resolver resolve(PicoContainer container, ComponentAdapter<?> forAdapter,
                     ComponentAdapter<?> injecteeAdapter, Type expectedType, NameBinding expectedNameBinding,
                     boolean useNames, Annotation binding);

    /**
     * @return
     * @todo REMOVE ME
     */
    String getTargetName();

    /**
     * Verify that the Parameter can satisfy the expected type using the container
     *
     * @param container           the container from which dependencies are resolved.
     * @param adapter             the {@link ComponentAdapter} that is asking for the verification
     * @param expectedType        the required type
     * @param expectedNameBinding Expected parameter name
     * @param useNames
     * @param binding
     * @throws PicoCompositionException if parameter and its dependencies cannot be resolved
     */
    void verify(PicoContainer container, ComponentAdapter<?> adapter,
                Type expectedType, NameBinding expectedNameBinding,
                boolean useNames, Annotation binding);

    /**
     * Accepts a visitor for this Parameter. The method is normally called by visiting a {@link ComponentAdapter}, that
     * cascades the {@linkplain PicoVisitor visitor} also down to all its {@linkplain Parameter Parameters}.
     *
     * @param visitor the visitor.
     */
    void accept(PicoVisitor visitor);


    /**
     * Resolver is used transitarily during resolving of Parameters.
     * isResolvable() and resolveInstance() in series do not cause resolveAdapter() twice
     */
    interface Resolver {

        /**
         * @return can the parameter be resolved
         */
        boolean isResolved();

        /**
         * @param into
         * @return the instance to be used to inject as a parameter
         */
        Object resolveInstance(Type into);

        /**
         * @return the ComponentAdapter for the parameter in question
         */
        ComponentAdapter<?> getComponentAdapter();

    }

    /**
     * The Parameter cannot (ever) be resolved
     */
    class NotResolved implements Resolver {
        public boolean isResolved() {
            return false;
        }

        public Object resolveInstance(final Type into) {
            return null;
        }

        public ComponentAdapter<?> getComponentAdapter() {
            return null;
        }
    }

    /**
     * Delegate to another reolver
     */
    abstract class DelegateResolver implements Resolver {
        private final Resolver delegate;

        public DelegateResolver(final Resolver delegate) {
            this.delegate = delegate;
        }

        public boolean isResolved() {
            return delegate.isResolved();
        }

        public Object resolveInstance(final Type into) {
            return delegate.resolveInstance(into);
        }

        public ComponentAdapter<?> getComponentAdapter() {
            return delegate.getComponentAdapter();
        }
    }

    /**
     * A fixed value wrapped as a Resolver
     */
    class ValueResolver<T> implements Resolver {

        private final boolean resolvable;
        private final T value;
        private final ComponentAdapter<?> adapter;

        public ValueResolver(final boolean resolvable, final T value, final ComponentAdapter<?> adapter) {
            this.resolvable = resolvable;
            this.value = value;
            this.adapter = adapter;
        }

        public boolean isResolved() {
            return resolvable;
        }

        public Object resolveInstance(final Type into) {
            return value;
        }

        public ComponentAdapter<?> getComponentAdapter() {
            return adapter;
        }
    }

}
