package org.kopptech.commonbond.bindings;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.kopptech.commonbond.Binding;
import org.kopptech.commonbond.ObjUtils;



public abstract class AbstractDirectPropertyBinding implements PropertyBinding
{
    protected Binding parentBinding;
    protected String propName;
    private Class<?> newInstanceClassOverride;
    private Set<ConstraintViolation<?>> constraintViolations;
    private boolean allowConstraintViolations = false;
    
    public AbstractDirectPropertyBinding(Binding parentBinding, String propName, Class<?> newInstanceClassOverride)
    {
        super();
        this.propName = propName;
        this.newInstanceClassOverride = newInstanceClassOverride;
        this.parentBinding = parentBinding;
        if(parentBinding==null) throw new RuntimeException("parentBinding cannot be null");
    }
    
    public abstract void setValueInternal(Object o);
    
    @SuppressWarnings({ "unchecked" })
    public void setValue(Object o, boolean allowInvalidValues)
    {
        constraintViolations = validateProperty(o);
        if(constraintViolations.size()==0 || allowInvalidValues || parentBinding.isAllowConstraintViolations()) setValueInternal(o);
    }

    @SuppressWarnings({ "rawtypes" })
    protected Set validateProperty(Object o)
    {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Class<?> parentBindingClass = determineParentBindingClass();
        return (Set)validator.validateValue(parentBindingClass, propName, o);
    }
    
    private Class<?> determineParentBindingClass()
    {
        if(parentBinding.getValue(false)!=null) return parentBinding.getValue(false).getClass();
        else return (Class<?>) parentBinding.getType();
    }

    public final Type getType()
    {
        if(newInstanceClassOverride!=null) return newInstanceClassOverride;
        return getDetectedTypeFromProperty();
    }
    
    public abstract Type getDetectedTypeFromProperty();
    
    
    public Object createDefaultValue()
    {
        try
        {
            Constructor<?> c = ((Class<?>)getType()).getConstructor();
            c.setAccessible(true);
            return c.newInstance();
        }
        catch (Exception e)
        {
            throw ObjUtils.wrap(e);
        }
    }
    
    public Binding getParentBinding()
    {
        return parentBinding;
    }
    public void setParentBinding(Binding parentBinding)
    {
        this.parentBinding = parentBinding;
    }
    public String getPropName()
    {
        return propName;
    }
    public void setPropName(String propName)
    {
        this.propName = propName;
    }

    public Class<?> getNewInstanceClassOverride()
    {
        return newInstanceClassOverride;
    }

    public void setNewInstanceClassOverride(Class<?> newInstanceClassOverride)
    {
        this.newInstanceClassOverride = newInstanceClassOverride;
    }

    public Set<ConstraintViolation<?>> getConstraintViolations()
    {
        return constraintViolations;
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
