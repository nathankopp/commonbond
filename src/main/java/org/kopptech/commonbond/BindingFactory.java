package org.kopptech.commonbond;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.kopptech.commonbond.bindings.DummyBinding;
import org.kopptech.commonbond.bindings.ElBinding;
import org.kopptech.commonbond.bindings.FieldBinding;
import org.kopptech.commonbond.bindings.GetterSetterBinding;
import org.kopptech.commonbond.bindings.InstanceBinding;
import org.kopptech.commonbond.bindings.ListBinding;
import org.kopptech.commonbond.bindings.OgnlBinding;


public class BindingFactory
{
    public Map<String, Class<? extends Binding>> map = new HashMap<String, Class<? extends Binding>>();
    
    public BindingFactory()
    {
        map.put("_instance", InstanceBinding.class);
        map.put("_list", ListBinding.class);
        map.put("_map", ListBinding.class);
        map.put("_dummy", DummyBinding.class);
        map.put("var", FieldBinding.class);
        map.put("prop", GetterSetterBinding.class);
        map.put("ognl", OgnlBinding.class);
        map.put("el", ElBinding.class);
    }
    
    public void register(String name, Class<? extends Binding> binding)
    {
        map.put(name, binding);
    }
    
    public Binding createBinding(String bindingName, Binding parent, String fieldName, Class<?> instanceType)
    {
        Class<? extends Binding> bindingClass = map.get(bindingName);
        try
        {
            Constructor<? extends Binding> c = bindingClass.getConstructor(Binding.class, String.class, Class.class);
            return c.newInstance(parent, fieldName, instanceType);
        }
        catch (Exception e)
        {
            throw ObjUtils.wrap(e);
        }
    }
}
