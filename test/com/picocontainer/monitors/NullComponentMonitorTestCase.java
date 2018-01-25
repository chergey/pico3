/*****************************************************************************
 * Copyright (C) 2003-2011 PicoContainer Committers. All rights reserved.    *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package com.picocontainer.monitors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static com.picocontainer.tck.MockFactory.mockeryWithCountingNamingScheme;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.picocontainer.ComponentAdapter;
import com.picocontainer.MutablePicoContainer;
import com.picocontainer.exceptions.PicoLifecycleException;

@RunWith(JMock.class)
public class NullComponentMonitorTestCase {

    private final Mockery mockery = mockeryWithCountingNamingScheme();

    @Test
    public void testItAll() throws NoSuchMethodException {

        NullComponentMonitor ncm = new NullComponentMonitor();
        Constructor<Object> constructor = (Constructor<Object>) makeConstructor();
        ComponentAdapter<Object> componentAdapter = (ComponentAdapter<Object>) makeCA();
        ncm.instantiated(makePico(), componentAdapter, constructor, "foo", new Object[0], 10);
        assertEquals(makeConstructor(), ncm.instantiating(makePico(), componentAdapter, constructor));
        ncm.instantiationFailed(makePico(), componentAdapter, constructor, new Exception());
        ncm.invocationFailed(makeConstructor(), "foo", new Exception());
        ncm.invoked(makePico(), makeCA(), makeMethod(), "foo", 10, null, new Object[0]);
        ncm.invoking(makePico(), makeCA(), makeMethod(), "foo", new Object[0]);
        try {
            ncm.lifecycleInvocationFailed(makePico(), makeCA(), makeMethod(), "foo", new RuntimeException());
        } catch (PicoLifecycleException e) {
            assertEquals(makeMethod(), e.getMethod());
            assertEquals("foo", e.getInstance());
            assertEquals("PicoLifecycleException: method 'public java.lang.String java.lang.String.toString()', instance 'foo, java.lang.RuntimeException", e.getMessage());
        }
        assertNull(ncm.noComponentFound(makePico(), String.class));

    }

    private MutablePicoContainer makePico() {
        return mockery.mock(MutablePicoContainer.class);
    }

    private ComponentAdapter<?> makeCA() {
        return mockery.mock(ComponentAdapter.class);
    }

    private Constructor<?> makeConstructor() {
        return String.class.getConstructors()[0];
    }

    private Method makeMethod() throws NoSuchMethodException {
        return String.class.getMethod("toString");
    }


}
