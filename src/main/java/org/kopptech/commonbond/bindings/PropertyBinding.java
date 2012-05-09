package org.kopptech.commonbond.bindings;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.kopptech.commonbond.Binding;



public interface PropertyBinding extends Binding
{
    public String getPropName();
    public Set<ConstraintViolation<?>> getConstraintViolations();
}
