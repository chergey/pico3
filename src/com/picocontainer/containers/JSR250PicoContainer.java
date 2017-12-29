/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.picocontainer.containers;

import com.picocontainer.MutablePicoContainer;
import com.picocontainer.PicoContainer;
import com.picocontainer.injectors.StaticsInitializedReferenceSet;
import com.picocontainer.monitors.NullComponentMonitor;

/**
 * Same as JSR330PicoContainer, only backed by ResourceRegistry
 */
public class JSR250PicoContainer extends JSR330PicoContainer {

    public JSR250PicoContainer(final PicoContainer parent, final Class<?> resourceRegistryClazz) {
        super(parent, new NullComponentMonitor(), new StaticsInitializedReferenceSet());
        addComponent(resourceRegistryClazz);
    }

    public JSR250PicoContainer(final MutablePicoContainer delegate, final Class<?> resourceRegistryClazz) {
        super(delegate);
        addComponent(resourceRegistryClazz);
    }

}
