/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.picocontainer.converters;

import java.net.MalformedURLException;
import java.net.URL;

import com.picocontainer.exceptions.PicoCompositionException;

/**
 * Converts values to URL data type objects
 *
 * @author Paul Hammant
 * @author Michael Rimov
 */
public class UrlConverter implements Converter<URL> {

    public URL convert(final String paramValue) {
        try {
            return new URL(paramValue);
        } catch (MalformedURLException e) {
            throw new PicoCompositionException(e);
        }
    }
}
