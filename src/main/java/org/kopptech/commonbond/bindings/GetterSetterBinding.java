package org.kopptech.commonbond.bindings;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.kopptech.commonbond.Binding;
import org.kopptech.commonbond.ObjUtils;


public class GetterSetterBinding extends AbstractDirectPropertyBinding
{
    public GetterSetterBinding(Binding parentBinding, String propName)
    {
        super(parentBinding, propName, null);
    }

    public GetterSetterBinding(Binding parentBinding, String propName, Class<?> newInstanceClassOverride)
    {
        super(parentBinding, propName, newInstanceClassOverride);
    }
    
    @Override
    public Type getDetectedTypeFromProperty()
    {
        Method m = getBoundGetMethod(false);
        return m.getGenericReturnType();
    }


    public Object getValue(boolean createParentIfNull)
    {
        if(parentBinding.getValue(createParentIfNull)==null) return null;
        Method m = getBoundGetMethod(createParentIfNull);
        try
        {
            return m.invoke(parentBinding.getValue(createParentIfNull));
        }
        catch (Exception e)
        {
            throw ObjUtils.wrap(e);
        }
    }

    @Override
    public void setValueInternal(Object obj)
    {
        Method m = getBoundSetMethod(true);
        try
        {
            m.invoke(parentBinding.getValue(true), obj);
        }
        catch (Exception e)
        {
            throw ObjUtils.wrap(e);
        }
    }

    public Method getBoundGetMethod(boolean createParentIfNull)
    {
        return ObjUtils.findGetterMethod(parentBinding.getValue(createParentIfNull).getClass(), propName);
    }
    
    public Method getBoundSetMethod(boolean createParentIfNull)
    {
        if(parentBinding.getValue(createParentIfNull)==null)
            parentBinding.setValue(parentBinding.createDefaultValue(), true);
        
        return ObjUtils.findSetterMethod(parentBinding.getValue(createParentIfNull).getClass(), propName);
    }
}