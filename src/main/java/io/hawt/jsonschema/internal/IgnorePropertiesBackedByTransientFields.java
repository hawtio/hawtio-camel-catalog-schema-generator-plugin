package io.hawt.jsonschema.internal;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;

/**
 * Ignores getters which are backed with a field of the same name which is a
 * transient field
 */
public class IgnorePropertiesBackedByTransientFields
        implements VisibilityChecker<IgnorePropertiesBackedByTransientFields> {
    private static final transient Logger LOG = LoggerFactory.getLogger(IgnorePropertiesBackedByTransientFields.class);

    private final VisibilityChecker<?> defaultChecker;

    public IgnorePropertiesBackedByTransientFields(VisibilityChecker<?> defaultChecker) {
        this.defaultChecker = defaultChecker;
    }

    @Override
    public boolean isGetterVisible(AnnotatedMethod method) {
        boolean answer = defaultChecker.isGetterVisible(method);
        if (answer) {
            answer = isGetterMethodWithFieldVisible(method, getGetterFieldName(method.getName()),
                    method.getDeclaringClass())
                    && isGetterMethodRetItselfVisible(method.getMember(), method.getDeclaringClass());
        }
        return answer;
    }

    @Override
    public boolean isGetterVisible(Method method) {
        boolean answer = defaultChecker.isGetterVisible(method);
        if (answer) {
            answer = isGetterMethodWithFieldVisible(method, getGetterFieldName(method.getName()),
                    method.getDeclaringClass()) && isGetterMethodRetItselfVisible(method, method.getDeclaringClass());
        }
        return answer;
    }

    @Override
    public boolean isIsGetterVisible(AnnotatedMethod method) {
        boolean answer = defaultChecker.isIsGetterVisible(method);
        if (answer) {
            answer = isGetterMethodWithFieldVisible(method, getIsGetterFieldName(method.getName()),
                    method.getDeclaringClass())
                    && isGetterMethodRetItselfVisible(method.getMember(), method.getDeclaringClass());
        }
        return answer;
    }

    @Override
    public boolean isIsGetterVisible(Method method) {
        boolean answer = defaultChecker.isIsGetterVisible(method);
        if (answer) {
            answer = isGetterMethodWithFieldVisible(method, getIsGetterFieldName(method.getName()),
                    method.getDeclaringClass()) && isGetterMethodRetItselfVisible(method, method.getDeclaringClass());
        }
        return answer;
    }

    protected String getIsGetterFieldName(String methodName) {
        return Introspector.decapitalize(methodName.substring(2));
    }

    protected String getGetterFieldName(String methodName) {
        return Introspector.decapitalize(methodName.substring(3));
    }

    /**
     * Returns false if the getter method has a field of the same name which is
     * transient
     * 
     * @return
     */
    protected boolean isGetterMethodWithFieldVisible(Object method, String fieldName, Class<?> declaringClass) {
        Field field = findField(fieldName, declaringClass);
        if (field != null) {
            int fieldModifiers = field.getModifiers();
            if (Modifier.isTransient(fieldModifiers)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Ignoring getter " + method + " due to transient field called " + fieldName);
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Returns false if the getter method just return the declaringClass itself to
     * avoid the recusive dead loop
     * 
     * @return
     */
    protected boolean isGetterMethodRetItselfVisible(Method method, Class<?> declaringClass) {
        if (method != null && method.getReturnType().getName().equals(declaringClass.getName())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Ignoring getter " + method + " due to return same type as declaringClass itself");
            }
            return false;
        }
        if (method != null && Throwable.class.isAssignableFrom(method.getReturnType())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Ignoring getter " + method + " due to return Throwable class");
            }
            return false;
        }
        return true;
    }

    // Delegated methods
    // -------------------------------------------------------------------------

    @Override
    public boolean isCreatorVisible(AnnotatedMember m) {
        return defaultChecker.isCreatorVisible(m);
    }

    @Override
    public boolean isCreatorVisible(Member m) {
        return defaultChecker.isCreatorVisible(m);
    }

    @Override
    public boolean isFieldVisible(AnnotatedField f) {
        return defaultChecker.isFieldVisible(f);
    }

    @Override
    public boolean isFieldVisible(Field f) {
        return defaultChecker.isFieldVisible(f);
    }

    @Override
    public boolean isSetterVisible(AnnotatedMethod m) {
        return defaultChecker.isSetterVisible(m);
    }

    @Override
    public boolean isSetterVisible(Method m) {
        return defaultChecker.isSetterVisible(m);
    }

    @Override
    public IgnorePropertiesBackedByTransientFields with(JsonAutoDetect ann) {
        return castToPropertiesBackedByTransientFields(defaultChecker.with(ann));
    }

    @Override
    public IgnorePropertiesBackedByTransientFields with(JsonAutoDetect.Visibility v) {
        return castToPropertiesBackedByTransientFields(defaultChecker.with(v));
    }

    @Override
    public IgnorePropertiesBackedByTransientFields withCreatorVisibility(JsonAutoDetect.Visibility v) {
        return castToPropertiesBackedByTransientFields(defaultChecker.withCreatorVisibility(v));
    }

    @Override
    public IgnorePropertiesBackedByTransientFields withFieldVisibility(JsonAutoDetect.Visibility v) {
        return castToPropertiesBackedByTransientFields(defaultChecker.withFieldVisibility(v));
    }

    @Override
    public IgnorePropertiesBackedByTransientFields withGetterVisibility(JsonAutoDetect.Visibility v) {
        return castToPropertiesBackedByTransientFields(defaultChecker.withGetterVisibility(v));
    }

    @Override
    public IgnorePropertiesBackedByTransientFields withIsGetterVisibility(JsonAutoDetect.Visibility v) {
        return castToPropertiesBackedByTransientFields(defaultChecker.withIsGetterVisibility(v));
    }

    @Override
    public IgnorePropertiesBackedByTransientFields withOverrides(JsonAutoDetect.Value v) {
        return castToPropertiesBackedByTransientFields(defaultChecker.withOverrides(v));
    }

    @Override
    public IgnorePropertiesBackedByTransientFields withSetterVisibility(JsonAutoDetect.Visibility v) {
        return castToPropertiesBackedByTransientFields(defaultChecker.withSetterVisibility(v));
    }

    @Override
    public IgnorePropertiesBackedByTransientFields withVisibility(PropertyAccessor method,
            JsonAutoDetect.Visibility v) {
        return castToPropertiesBackedByTransientFields(defaultChecker.withVisibility(method, v));
    }

    protected IgnorePropertiesBackedByTransientFields castToPropertiesBackedByTransientFields(Object value) {
        if (value instanceof IgnorePropertiesBackedByTransientFields) {
            return (IgnorePropertiesBackedByTransientFields) value;
        } else {
            if (value != null) {
                if (value instanceof VisibilityChecker<?>) {
                    return new IgnorePropertiesBackedByTransientFields((VisibilityChecker<?>) value);
                }
                // System.out.println("Could not convert value to
                // IgnorePropertiesBackedByTransientFields as was " + value.getClass().getName()
                // + " " + value);
                LOG.warn("Could not convert value to IgnorePropertiesBackedByTransientFields as was "
                        + value.getClass().getName() + " " + value);
            }
            return null;
        }
    }

    protected static Field findField(String fieldName, Class<?> declaringClass) {
        try {
            return declaringClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superclass = declaringClass.getSuperclass();
            if (superclass != null && superclass != declaringClass) {
                return findField(fieldName, superclass);
            } else {
                return null;
            }
        }
    }

}
