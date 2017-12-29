/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.picocontainer.exceptions;

import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class PicoLifecycleException extends PicoException {

    private final Method method;
    private final Object instance;

    public PicoLifecycleException(final Method method, final Object instance, final Throwable cause) {
        super(cause);
        this.method = method;
        this.instance = instance;
    }

    public Method getMethod() {
        return method;
    }

    public Object getInstance() {
        return instance;
    }

    @Override
	public String getMessage() {
        return "PicoLifecycleException: method '" + method + "', instance '"+ instance + ", " + super.getMessage();
    }

}
