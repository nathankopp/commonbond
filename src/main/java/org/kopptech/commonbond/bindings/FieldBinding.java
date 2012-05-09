package org.kopptech.commonbond.bindings;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.kopptech.commonbond.Binding;
import org.kopptech.commonbond.ObjUtils;


public class FieldBinding extends AbstractDirectPropertyBinding
{
    public FieldBinding(Binding parentBinding, String propName)
    {
        super(parentBinding, propName, null);
    }

    public FieldBinding(Binding parentBinding, String propName, Class<?> newInstanceClassOverride)
    {
        super(parentBinding, propName, newInstanceClassOverride);
    }
    
    @Override
    public Type getDetectedTypeFromProperty()
    {
        Field f = getBoundField(false);
        return f.getGenericType();
    }


    public Object getValue(boolean createParentIfNull)
    {
        Field f = getBoundField(createParentIfNull);
        if(f==null) return null;
        if(parentBinding.getValue(createParentIfNull)==null) return null;
        try
        {
            return f.get(parentBinding.getValue(createParentIfNull));
        }
        catch (Exception e)
        {
            throw ObjUtils.wrap(e);
        }
    }
    
    

    @Override
    public void setValueInternal(Object obj)
    {
        Field f = getBoundField(true);
        try
        {
            obj = ObjUtils.convertIfNecessary(obj, f.getType());
            try
            {
            f.set(parentBinding.getValue(true), obj);
            }
            catch(RuntimeException e)
            {
                throw new RuntimeException("problem with "+parentBinding.getClass().getName(),e);
            }
        }
        catch (Exception e)
        {
            throw ObjUtils.wrap(e);
        }
    }

    public Field getBoundField()
    {
        return getBoundField(false);
    }
    
    public Field getBoundField(boolean createParentIfNull)
    {
        Object value = parentBinding.getValue(createParentIfNull);
        if(value==null)
        {
            value = parentBinding.createDefaultValue();
            if(value==null) throw new RuntimeException("could not create default value for parentbinding "+parentBinding.getType()+" "+parentBinding.getClass().getName());
            
            if(createParentIfNull)
            {
                parentBinding.setValue(value, true);
            }
        }        
        Field f = ObjUtils.findDeclaredField(value.getClass(), propName);
        if(f==null) throw new RuntimeException("field "+propName+" not found in object of type "+value.getClass().getName());
        f.setAccessible(true);
        return f;
    }
}
