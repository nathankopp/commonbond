package org.kopptech.commonbond.bindings;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.kopptech.commonbond.Binding;
import org.kopptech.commonbond.InputStyle;
import org.kopptech.commonbond.ObjUtils;


public class ListBinding implements PropertyBinding
{
    private InputStyle inputStyle;
    private Binding rootBinding;
    private int currentIdx = -1;
    private Class<?> instanceTypeOverride;
    
    public ListBinding(PropertyBinding rootBinding)
    {
        this.rootBinding = rootBinding;
    }

    public ListBinding(Binding rootBinding, InputStyle inputStyle, Class<?> newInstanceClassOverride)
    {
        this.inputStyle = inputStyle;
        this.rootBinding = rootBinding;
        this.instanceTypeOverride = newInstanceClassOverride;
    }
    
    public Type getType()
    {
        if(instanceTypeOverride!=null) return instanceTypeOverride;
        Type type = rootBinding.getType();
        if(type instanceof ParameterizedType)
        {
            ParameterizedType ptype = (ParameterizedType)type;
            Type[] types = ptype.getActualTypeArguments();
            if(types.length==0) return null;
            return types[0];
        }
        else
        {
            throw new RuntimeException("type cannot be detected");
        }
    }
    
    
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
    
    public void addNewValueAndSetAsCurrent(Object value)
    {
        int idx = getAssociatedListObject().size();
        setCurrentIdx(idx);
        setValue(value==null?createDefaultValue():value, true);
    }

    
    @SuppressWarnings("unchecked")
    public List<Object> getAssociatedListObject()
    {
        List<Object> list = (List<Object>)rootBinding.getValue(true);
        if(list==null)
        {
            list = new ArrayList<Object>();
            rootBinding.setValue(list, true);
        }
        return list;
    }

    public Object getValue(boolean createParentIfNull)
    {
        if(currentIdx<0) return null;
        if(currentIdx>=getAssociatedListObject().size())
        {
            if(createParentIfNull)
            {
                setValue(createDefaultValue(),true);
            }
            else
            {
                return null;
            }
        }
        return getAssociatedListObject().get(currentIdx);
    }

    public void setValue(Object obj, boolean allowInvalidValues)
    {
        List<Object> list = getAssociatedListObject();
        if(currentIdx==-1)
        {
            throw new RuntimeException("shouldn't be here");
            // FIXME; note: this is a special case for EL to determine they type
            /*
            com.mostlynumbers.binding.bindings.ListBinding.setValue(ListBinding.java:107)
            com.mostlynumbers.binding.bindings.FieldBinding.getBoundField(FieldBinding.java:68)
            com.mostlynumbers.binding.bindings.FieldBinding.getBoundField(FieldBinding.java:62)
            com.mostlynumbers.binding.bindings.FieldBinding.getValue(FieldBinding.java:32)
            com.mostlynumbers.binding.bindings.ElBinding.createContext(ElBinding.java:81)
            com.mostlynumbers.binding.bindings.ElBinding.<init>(ElBinding.java:29)
            com.mostlynumbers.binding.BindType$5.createBinding(BindType.java:42)
            com.mostlynumbers.binding.Binder.bindValue(Binder.java:71)
            com.mostlynumbers.binding.json.JsonBindingReader.readBindingNode(JsonBindingReader.java:113)
            com.mostlynumbers.binding.json.JsonBindingReader.readBindingNode(JsonBindingReader.java:100)
            com.mostlynumbers.binding.json.JsonBindingReader.readListBinding(JsonBindingReader.java:39)
            */
//            list.add(obj);
//            currentIdx = list.size()-1;
        }
        else if(currentIdx>=list.size())
        {
            list.add(obj);
            currentIdx = list.size()-1;
        }
        else
        {
            list.set(currentIdx, obj);
        }
    }

    public String getPropName()
    {
        if(!(rootBinding instanceof PropertyBinding)) return "root";
        return ((PropertyBinding)rootBinding).getPropName();
    }
    
    public Set<ConstraintViolation<?>> getConstraintViolations()
    {
        if(rootBinding instanceof PropertyBinding)
            return ((PropertyBinding)rootBinding).getConstraintViolations();
        else
            return null;
    }
    
    public boolean isAllowConstraintViolations()
    {
        return rootBinding.isAllowConstraintViolations();
    }
    
    public void setAllowConstraintViolations(boolean allowConstraintViolations)
    {
        rootBinding.setAllowConstraintViolations(allowConstraintViolations);
    }

    public int findIdxById(String id)
    {
        List<Object> list = getAssociatedListObject();
        for(int i=0; i<list.size(); i++)
        {
            Object o = list.get(i);
            String id2 = ObjUtils.findId(o);
            if(id.equals(id2)) return i;
        }
        return -1;
    }


    public void deleteRemaining()
    {
        List<Object> list = getAssociatedListObject();
        while((currentIdx+1)<list.size()) list.remove(currentIdx+1);
    }
    
    public InputStyle getInputStyle()
    {
        return inputStyle;
    }

    public void setInputStyle(InputStyle inputStyle)
    {
        this.inputStyle = inputStyle;
    }

    public Binding getRootBinding()
    {
        return rootBinding;
    }

    public void setRootBinding(Binding rootBinding)
    {
        this.rootBinding = rootBinding;
    }

    public int getCurrentIdx()
    {
        return currentIdx;
    }

    public void setCurrentIdx(int currentIdx)
    {
        this.currentIdx = currentIdx;
    }




}
