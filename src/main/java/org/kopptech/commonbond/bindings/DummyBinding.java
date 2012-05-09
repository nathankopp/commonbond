package org.kopptech.commonbond.bindings;

import java.lang.reflect.Type;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.kopptech.commonbond.Binding;



public class DummyBinding implements PropertyBinding
{
    protected Binding parentBinding;
    
    public DummyBinding(Binding parentBinding)
    {
        super();
        this.parentBinding = parentBinding;
    }
    
    public Object getValue(boolean createParentIfNull)
    {
        return parentBinding.getValue(createParentIfNull);
    }

    public Type getType()
    {
        return parentBinding.getType();
    }

    public void setValue(Object o, boolean allowInvalidValues)
    {
        parentBinding.setValue(o, allowInvalidValues);
    }

    public Object createDefaultValue()
    {
        return parentBinding.createDefaultValue();
    }

    public boolean isAllowConstraintViolations()
    {
        return parentBinding.isAllowConstraintViolations();
    }

    public void setAllowConstraintViolations(boolean allowConstraintViolations)
    {
        parentBinding.setAllowConstraintViolations(allowConstraintViolations);
    }

    public String getPropName()
    {
        if(parentBinding instanceof PropertyBinding)
            return ((PropertyBinding) parentBinding).getPropName();
        else
            return null;
    }

    public Set<ConstraintViolation<?>> getConstraintViolations()
    {
        if(parentBinding instanceof PropertyBinding)
            return ((PropertyBinding) parentBinding).getConstraintViolations();
        else
            return null;
    }



}
