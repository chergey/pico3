/*
 * Copyright 2008 - 2017 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.picocontainer.injectors;

import static com.picocontainer.injectors.AnnotatedMethodInjection.getInjectionAnnotation;
import static com.picocontainer.injectors.AnnotatedMethodInjection.AnnotatedMethodInjector.makeAnnotationNames;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.picocontainer.Characteristics;
import com.picocontainer.ComponentAdapter;
import com.picocontainer.ComponentMonitor;
import com.picocontainer.LifecycleStrategy;
import com.picocontainer.NameBinding;
import com.picocontainer.Parameter;
import com.picocontainer.exceptions.PicoCompositionException;
import com.picocontainer.annotations.Bind;
import com.picocontainer.behaviors.AbstractBehavior;
import com.picocontainer.parameters.ConstructorParameters;
import com.picocontainer.parameters.FieldParameters;
import com.picocontainer.parameters.JSR330ComponentParameter;
import com.picocontainer.parameters.MethodParameters;


/**
 * A {@link com.picocontainer.InjectionType} for Guice-style annotated fields.
 * The factory creates {@link AnnotatedFieldInjector}.
 *
 * @author Paul Hammant
 */
@SuppressWarnings("serial")
public class AnnotatedFieldInjection extends AbstractInjectionType {

    private final Class<? extends Annotation>[] injectionAnnotations;


    public AnnotatedFieldInjection(final Class<? extends Annotation>... injectionAnnotations) {
        this.injectionAnnotations = injectionAnnotations;
    }

    @SuppressWarnings("unchecked")
    public AnnotatedFieldInjection() {
        this(getInjectionAnnotation("javax.inject.Inject"), getInjectionAnnotation("Inject"),
                getInjectionAnnotation("javax.annotation.Resource"), getInjectionAnnotation("javax.inject.Named"));
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> ComponentAdapter<T> createComponentAdapter(final ComponentMonitor monitor,
                                                          final LifecycleStrategy lifecycle,
                                                          final Properties componentProps,
                                                          final Object key,
                                                          final Class<T> impl,
                                                          final ConstructorParameters constructorParams, final FieldParameters[] fieldParams, final MethodParameters[] methodParams) throws PicoCompositionException {
        boolean useNames = AbstractBehavior.arePropertiesPresent(componentProps, Characteristics.USE_NAMES, true);

        boolean requireConsumptionOfAllParameters = !(AbstractBehavior.arePropertiesPresent(componentProps, Characteristics.ALLOW_UNUSED_PARAMETERS, false));

        return wrapLifeCycle(monitor.newInjector(new AnnotatedFieldInjector(key, impl, fieldParams, monitor,
                useNames, requireConsumptionOfAllParameters, injectionAnnotations)), lifecycle);
    }

    /**
     * Injection happens after instantiation, and through fields marked as injection points via an Annotation.
     * The default annotation of com.picocontainer.annotations.@Inject can be overridden.
     */
    public static class AnnotatedFieldInjector<T> extends AbstractFieldInjector<T> {

        private final Class<? extends Annotation>[] injectionAnnotations;
        private String injectionAnnotationNames;

        public AnnotatedFieldInjector(final Object key, final Class<T> impl, final FieldParameters[] parameters, final ComponentMonitor monitor,
                                      final boolean useNames, final boolean requireConsumptionOfAllParameters, final Class<? extends Annotation>... injectionAnnotations) {

            super(key, impl, monitor, useNames, requireConsumptionOfAllParameters, parameters);
            this.injectionAnnotations = injectionAnnotations;
        }

        @Override
        protected void initializeInjectionMembersAndTypeLists() {
            injectionMembers = new ArrayList<>();
            List<Annotation> bindingIds = new ArrayList<>();
            final List<Type> typeList = new ArrayList<>();
            Class<?> drillInto = getComponentImplementation();
            while (drillInto != Object.class) {
                final Field[] fields = getFields(drillInto);
                for (final Field field : fields) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }

                    if (isAnnotatedForInjection(field)) {
                        injectionMembers.add(field);
                    }
                }
                drillInto = drillInto.getSuperclass();
            }

            //Sort for injection.
            Collections.sort(injectionMembers, new JSR330AccessibleObjectOrderComparator());
            for (AccessibleObject eachMember : injectionMembers) {
                Field field = (Field) eachMember;
                typeList.add(box(field.getGenericType()));
                bindingIds.add(getBinding(field));

            }

            injectionTypes = typeList.toArray(new Type[0]);
            bindings = bindingIds.toArray(new Annotation[0]);

        }

        /**
         * Sorry, can't figure out how else to test injection member order without
         * this function or some other ugly hack to get at the private data structure.
         * At least I made it read only?  :D  -MR
         *
         * @return
         */
        @SuppressWarnings("unchecked")
        public List<AccessibleObject> getInjectionMembers() {
            return injectionMembers != null ? Collections.unmodifiableList(injectionMembers) : Collections.EMPTY_LIST;
        }

        public static Annotation getBinding(final Field field) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().isAnnotationPresent(Bind.class)) {
                    return annotation;
                }
            }
            return null;
        }

        protected final boolean isAnnotatedForInjection(final Field field) {
            for (Class<? extends Annotation> injectionAnnotation : injectionAnnotations) {
                if (field.isAnnotationPresent(injectionAnnotation)) {
                    return true;
                }
            }
            return false;
        }


        private Field[] getFields(final Class<?> clazz) {
            return AccessController.doPrivileged((PrivilegedAction<Field[]>) clazz::getDeclaredFields);
        }

        /**
         * Allows Different swapping of types.
         *
         * @return
         */
        @Override
        protected Parameter constructDefaultComponentParameter() {
            return JSR330ComponentParameter.DEFAULT;
        }


        /**
         * Performs the actual injection.
         */
        @Override
        protected Object injectIntoMember(final AccessibleObject member, final Object componentInstance, final Object toInject)
                throws IllegalAccessException {
            final Field field = (Field) member;

            AnnotationInjectionUtils.setMemberAccessible(member);

            field.set(componentInstance, toInject);
            return null;
        }


        @Override
        protected Parameter[] interceptParametersToUse(final Parameter[] currentParameters, final AccessibleObject member) {
            return AnnotationInjectionUtils.interceptParametersToUse(currentParameters, member);
        }

        @Override
        public String getDescriptor() {
            if (injectionAnnotationNames == null) {
                injectionAnnotationNames = makeAnnotationNames(injectionAnnotations);
            }
            return "AnnotatedFieldInjector[" + injectionAnnotationNames + "]-";
        }


        @Override
        protected NameBinding makeParameterNameImpl(final AccessibleObject member) {
            return ((Field) member)::getName;
        }

        @Override
        protected Object memberInvocationReturn(final Object lastReturn, final AccessibleObject member, final Object instance) {
            return instance;
        }

    }
}
