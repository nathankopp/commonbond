package org.kopptech.commonbond.bindings;

import org.kopptech.commonbond.Binding;
import org.kopptech.commonbond.ObjUtils;

public class InstanceBinding implements Binding
{
    private Object value;
    private Class<?> newInstanceClass;
    private boolean allowConstraintViolations = false;
    
    public InstanceBinding(Class<?> newInstanceClass)
    {
        super();
        this.newInstanceClass = newInstanceClass;
    }

    public InstanceBinding(Object value)
    {
        super();
        this.value = value;
    }
    
    public Class<?> getType()
    {
        return value.getClass();
    }

    
    public Object createDefaultValue()
    {
        try
        {
            return newInstanceClass.newInstance();
        }
        catch (Exception e)
        {
            throw ObjUtils.wrap(e);
        }
    }

    public Object getValue(boolean createParentIfNull)
    {
        return value;
    }

    public void setValue(Object o, boolean allowInvalidValues)
    {
        value = o;
    }

    public Class<?> getNewInstanceClass()
    {
        return newInstanceClass;
    }

    public void setNewInstanceClass(Class<?> newInstanceClass)
    {
        this.newInstanceClass = newInstanceClass;
    }

    public boolean isAllowConstraintViolations()
    {
        return allowConstraintViolations;
    }

    public void setAllowConstraintViolations(boolean allowConstraintViolations)
    {
        this.allowConstraintViolations = allowConstraintViolations;
    }
    
}
