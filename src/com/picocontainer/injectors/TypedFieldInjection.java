/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.picocontainer.injectors;

import static com.picocontainer.Characteristics.immutable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.picocontainer.Characteristics;
import com.picocontainer.ComponentAdapter;
import com.picocontainer.ComponentMonitor;
import com.picocontainer.LifecycleStrategy;
import com.picocontainer.NameBinding;
import com.picocontainer.exceptions.PicoCompositionException;
import com.picocontainer.annotations.Bind;
import com.picocontainer.behaviors.AbstractBehavior;
import com.picocontainer.parameters.ConstructorParameters;
import com.picocontainer.parameters.FieldParameters;
import com.picocontainer.parameters.MethodParameters;

/**
 * A {@link com.picocontainer.InjectionType} for named fields.
 *
 * Use like so: pico.as(injectionFieldNames("field1", "field2")).addComponent(...)
 *
 * The factory creates {@link TypedFieldInjector}.
 *
 * @author Paul Hammant
 */
@SuppressWarnings("serial")
public class TypedFieldInjection extends AbstractInjectionType {

    private static final String INJECTION_FIELD_TYPES = "injectionFieldTypes";

	public <T> ComponentAdapter<T> createComponentAdapter(final ComponentMonitor monitor,
                                                   final LifecycleStrategy lifecycle,
                                                   final Properties componentProps,
                                                   final Object key,
                                                   final Class<T> impl,
                                                   final ConstructorParameters constructorParams, final FieldParameters[] fieldParams, final MethodParameters[] methodParams) throws PicoCompositionException {
        boolean requireConsumptionOfAllParameters = !(AbstractBehavior.arePropertiesPresent(componentProps, Characteristics.ALLOW_UNUSED_PARAMETERS, false));
        String fieldTypes = (String) componentProps.remove(INJECTION_FIELD_TYPES);
        if (fieldTypes == null) {
            fieldTypes = "";
        }
        return wrapLifeCycle(monitor.newInjector(new TypedFieldInjector<T>(key, impl, monitor, fieldTypes, requireConsumptionOfAllParameters, fieldParams
        )), lifecycle);
    }

    public static Properties injectionFieldTypes(final String... fieldTypes) {
        StringBuilder sb = new StringBuilder();
        for (String fieldType : fieldTypes) {
            sb.append(" ").append(fieldType);
        }
        return immutable(INJECTION_FIELD_TYPES, sb.toString().trim());
    }

    /**
     * Injection happens after instantiation, and fields are marked as
     * injection points via a field type.
     */
    public static class TypedFieldInjector<T> extends AbstractFieldInjector<T> {

        private final List<String> classes;

        public TypedFieldInjector(final Object key,
                                  final Class<?> impl,
                                  final ComponentMonitor monitor,
                                  final String classNames,
                                  final boolean requireUseOfallParameters,
                                  final FieldParameters... parameters) {
            super(key, impl, monitor, true, requireUseOfallParameters, parameters);
            this.classes = Arrays.asList(classNames.trim().split(" "));
        }

        @Override
        protected void initializeInjectionMembersAndTypeLists() {
            injectionMembers = new ArrayList<>();
            List<Annotation> bindingIds = new ArrayList<>();
            final List<Type> typeList = new ArrayList<>();
            final Field[] fields = getFields();
            for (final Field field : fields) {
                if (isTypedForInjection(field)) {
                    injectionMembers.add(field);
                    typeList.add(box(field.getType()));
                    bindingIds.add(getBinding(field));
                }
            }
            injectionTypes = typeList.toArray(new Type[0]);
            bindings = bindingIds.toArray(new Annotation[0]);
        }

        private Annotation getBinding(final Field field) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().isAnnotationPresent(Bind.class)) {
                    return annotation;
                }
            }
            return null;
        }

        protected boolean isTypedForInjection(final Field field) {
            return classes.contains(field.getType().getName());
        }

        private Field[] getFields() {
            return AccessController.doPrivileged((PrivilegedAction<Field[]>) () -> getComponentImplementation().getDeclaredFields());
        }


        @Override
		protected Object injectIntoMember(final AccessibleObject member, final Object componentInstance, final Object toInject)
            throws IllegalAccessException {
            Field field = (Field) member;
            field.setAccessible(true);
            field.set(componentInstance, toInject);
            return null;
        }

        @Override
        public String getDescriptor() {
            return "TypedFieldInjector-";
        }

        @Override
        protected NameBinding makeParameterNameImpl(final AccessibleObject member) {
            return ((Field) member)::getName;
        }

        @Override
		protected Object memberInvocationReturn(final Object lastReturn, final AccessibleObject member, final Object instance) {
            return instance;
        }

        List<String> getInjectionFieldTypes() {
            return Collections.unmodifiableList(classes);
        }


    }
}