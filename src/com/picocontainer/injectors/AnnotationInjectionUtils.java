/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.picocontainer.injectors;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;

import javax.annotation.Resource;
import javax.inject.Named;

import com.picocontainer.Parameter;
import com.picocontainer.exceptions.PicoCompositionException;
import com.picocontainer.containers.JSR330PicoContainer;
import com.picocontainer.parameters.ComponentParameter;
import com.picocontainer.parameters.JSR250ComponentParameter;
import com.picocontainer.parameters.JSR330ComponentParameter;

public class AnnotationInjectionUtils {

    private AnnotationInjectionUtils() {
    }


    /**
     * If a default ComponentParameter() is being used for a particular argument for the given method, then
     * this function may substitute what would normally be resolved based on JSR-330 annotations.
     */
    public static Parameter[] interceptParametersToUse(final Parameter[] currentParameters, final AccessibleObject member) {
        Annotation[][] allAnnotations = getParameterAnnotations(member);

        if (currentParameters.length != allAnnotations.length) {
            throw new PicoCompositionException("Internal error, parameter lengths, not the same as the annotation lengths");
        }

        //Make this function side-effect free.
        Parameter[] returnValue = Arrays.copyOf(currentParameters, currentParameters.length);


        for (int i = 0; i < returnValue.length; i++) {
            //Allow composition scripts to override annotations
            //See comment in AnnotatedFieldInjection.AnnotatedFieldInjector.getParameterToUseForObject(AccessibleObject, AccessibleObjectParameterSet...)
            //for possible issues with this.
            if (returnValue[i] != ComponentParameter.DEFAULT && returnValue[i] != JSR330ComponentParameter.DEFAULT) {
                continue;
            }


            Named namedAnnotation = (Named) getAnnotationIfPresent(allAnnotations[i], Named.class);
            if (namedAnnotation != null) {
                returnValue[i] = new JSR330ComponentParameter(namedAnnotation.value());
            } else {
                Annotation qualifier = JSR330PicoContainer.getQualifier(allAnnotations[i]);
                if (qualifier != null) {
                    returnValue[i] = new JSR330ComponentParameter(qualifier.annotationType().getName());
                }
            }

            Resource resouceAnno = (Resource) getAnnotationIfPresent(allAnnotations[i], Resource.class);

            if (resouceAnno != null && member instanceof Field) {
                Class<?> clazz= ((Field) member).getType();
                returnValue[i] = new JSR250ComponentParameter(resouceAnno.name(), clazz);
            }

            //Otherwise don't modify it.
        }

        return returnValue;
    }


    private static Annotation[][] getParameterAnnotations(final AccessibleObject member) {
        if (member instanceof Constructor) {
            return ((Constructor<?>) member).getParameterAnnotations();
        } else if (member instanceof Field) {
            return new Annotation[][]{member.getAnnotations()};
        } else if (member instanceof Method) {
            return ((Method) member).getParameterAnnotations();
        } else {
            AbstractInjector.throwUnknownAccessibleObjectType(member);
            //Never gets here
            return null;
        }
    }


    private static Annotation getAnnotationIfPresent(final Annotation[] annotations, Class<?> anno) {
        for (Annotation eachAnnotation : annotations) {
            if (eachAnnotation.annotationType().equals(anno)) {
                return eachAnnotation;
            }
        }
        return null;
    }

    /**
     * Allows private method/constructor injection on fields/methods
     *
     * @param target
     */
    public static void setMemberAccessible(final AccessibleObject target) {
        //Don't run a privileged block if we don't have to.
        if (target.isAccessible()) {
            return;
        }

        AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
            target.setAccessible(true);
            return null;
        });
    }

}
