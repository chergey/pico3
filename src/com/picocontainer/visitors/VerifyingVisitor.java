/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.picocontainer.visitors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picocontainer.ComponentAdapter;
import com.picocontainer.ComponentFactory;
import com.picocontainer.Parameter;
import com.picocontainer.PicoContainer;
import com.picocontainer.exceptions.PicoVerificationException;
import com.picocontainer.PicoVisitor;


/**
 * Visitor to verify {@link PicoContainer} instances. The visitor walks down the logical container hierarchy.
 *
 * @author J&ouml;rg Schaible
 */
public class VerifyingVisitor extends TraversalCheckingVisitor {

    private final List<RuntimeException> nestedVerificationExceptions;
    private final Set<ComponentAdapter> verifiedComponentAdapters;
    private final Set<ComponentFactory> verifiedComponentFactories;
    private final PicoVisitor componentAdapterCollector;
    private PicoContainer currentPico;

    /**
     * Construct a VerifyingVisitor.
     */
    public VerifyingVisitor() {
        nestedVerificationExceptions = new ArrayList<RuntimeException>();
        verifiedComponentAdapters = new HashSet<ComponentAdapter>();
        verifiedComponentFactories = new HashSet<ComponentFactory>();
        componentAdapterCollector = new ComponentAdapterCollector();
    }

    /**
     * Traverse through all components of the {@link PicoContainer} hierarchy and verify the components.
     *
     * @throws PicoVerificationException if some components could not be verified.
     * @see com.picocontainer.PicoVisitor#traverse(java.lang.Object)
     */
    @Override
	public Object traverse(final Object node) throws PicoVerificationException {
        nestedVerificationExceptions.clear();
        verifiedComponentAdapters.clear();
        try {
            super.traverse(node);
            if (!nestedVerificationExceptions.isEmpty()) {
                throw new PicoVerificationException(new ArrayList<RuntimeException>(nestedVerificationExceptions));
            }
        } finally {
            nestedVerificationExceptions.clear();
            verifiedComponentAdapters.clear();
        }
        return Void.TYPE;
    }

    @Override
	public boolean visitContainer(final PicoContainer pico) {
        super.visitContainer(pico);
        currentPico = pico;
        return CONTINUE_TRAVERSAL;
    }

    @Override
	public void visitComponentAdapter(final ComponentAdapter<?> componentAdapter) {
        super.visitComponentAdapter(componentAdapter);
        if (!verifiedComponentAdapters.contains(componentAdapter)) {
            try {
                componentAdapter.verify(currentPico);
            } catch (RuntimeException e) {
                nestedVerificationExceptions.add(e);
            }
            componentAdapter.accept(componentAdapterCollector);
        }

    }

    @Override
	public void visitComponentFactory(final ComponentFactory componentFactory) {
        super.visitComponentFactory(componentFactory);

        if (!verifiedComponentFactories.contains(componentFactory)) {
            try {
                componentFactory.verify(currentPico);
            } catch (RuntimeException e) {
                nestedVerificationExceptions.add(e);
            }
            componentFactory.accept(componentAdapterCollector);
        }

    }



    private class ComponentAdapterCollector implements PicoVisitor {
        // /CLOVER:OFF
        public Object traverse(final Object node) {
            return null;
        }

        public boolean visitContainer(final PicoContainer pico) {
            return CONTINUE_TRAVERSAL;
        }

        // /CLOVER:ON

        public void visitComponentAdapter(final ComponentAdapter componentAdapter) {
            verifiedComponentAdapters.add(componentAdapter);
        }

        public void visitComponentFactory(final ComponentFactory componentFactory) {
            verifiedComponentFactories.add(componentFactory);
        }

        public void visitParameter(final Parameter parameter) {

        }
    }
}
