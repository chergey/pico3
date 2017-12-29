/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.picocontainer;

import java.io.Closeable;

public interface ResourceRegistry extends Closeable {
    <T> T get(String name, Class<T> clazz);

}
