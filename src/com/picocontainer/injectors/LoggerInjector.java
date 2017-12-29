/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.picocontainer.injectors;

import com.picocontainer.PicoContainer;
import com.picocontainer.exceptions.PicoCompositionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class LoggerInjector extends FactoryInjector<Logger> {
    public Logger getComponentInstance(PicoContainer container, final Type into) throws PicoCompositionException {
        return LoggerFactory.getLogger(((InjectInto) into).getIntoClass());
    }
}

