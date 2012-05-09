package org.kopptech.commonbond.bindings;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import org.kopptech.commonbond.Binding;


public class MapBinding extends AbstractDirectPropertyBinding
{
    public MapBinding(Binding parentBinding, String propName)
    {
        super(parentBinding, propName, null);
    }

    public MapBinding(Binding parentBinding, String propName, Class<?> newInstanceClassOverride)
    {
        super(parentBinding, propName, newInstanceClassOverride);
    }
    
    @Override
    public Type getDetectedTypeFromProperty()
    {
        Type type = parentBinding.getType();
        if(type instanceof ParameterizedType)
        {
            ParameterizedType ptype = (ParameterizedType)type;
            Type[] types = ptype.getActualTypeArguments();
            if(types.length<2) return null;
            return types[1];
        }
        else
        {
            throw new RuntimeException("type cannot be detected");
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object getValue(boolean createParentIfNull)
    {
        Map<Object,Object> map = (Map)parentBinding.getValue(createParentIfNull);
        return map.get(propName);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setValueInternal(Object obj)
    {
        Map<Object,Object> map = (Map)parentBinding.getValue(true);
        map.put(propName, obj);
    }
}
