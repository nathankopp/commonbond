package org.kopptech.commonbond;

import java.util.List;

import org.kopptech.commonbond.bindings.InstanceBinding;
import org.kopptech.commonbond.bindings.ListBinding;



public class Binder
{
    private BindingNode root;
    private BindingNode current;
    
    public Binder(Object obj)
    {
        root = new BindingNode(new InstanceBinding(obj));
        current = root;
    }
    
    public Binder(BindingNode root)
    {
        this.root = root;
        current = root;
    }
    
    public static Binder startWithObj(Object obj)
    {
        Binder binder = new Binder(obj);
        return binder;
    }
    
    public static Binder startWithList(List<?> list, InputStyle inputStyle, Class<?> instanceType)
    {
        BindingNode node = new BindingNode(new ListBinding(new InstanceBinding(list), inputStyle, instanceType));
        return new Binder(node);
    }
    
    public Binder setRoot(BindingNode root)
    {
        this.root = root;
        current = root;
        return this;
    }
    
    public Binder bindObject(String tagName, String fieldName, Class<?> instanceType)
    {
        return bindObject(tagName, fieldName, instanceType, false, BindType.AUTO);
    }
    public Binder bindObject(String tagName, String fieldName, Class<?> instanceType, boolean readOnly, BindType bindType)
    {
        BindingNode node = new BindingNode(tagName, bindType.createBinding(current.getBinding(), fieldName, instanceType));
        node.setReadOnly(readOnly);
        current.addChild(node);
        current = node;
        return this;
    }
    
    public Binder endObject()
    {
        if(current.getChildren()==null || current.getChildren().size()==0) throw new RuntimeException("endObject without any children");
        current = current.getParent();
        return this;
    }
    
    public Binder bindValue(String tagName, String fieldName)
    {
        return bindValue(tagName, fieldName, false, BindType.AUTO);
    }
    public Binder bindValue(String tagName, String fieldName, boolean readOnly, BindType bindType)
    {
        BindingNode node = new BindingNode(tagName, bindType.createBinding(current.getBinding(), fieldName, null));
        node.setReadOnly(readOnly);
        current.addChild(node);
        return this;
    }
    
    public Binder bindList(String tagName, String fieldName, InputStyle inputStyle, Class<?> instanceType)
    {
        return bindList(tagName, fieldName, inputStyle, instanceType, false, BindType.AUTO);
    }
    public Binder bindList(String tagName, String fieldName, InputStyle inputStyle, Class<?> instanceType, boolean readOnly, BindType bindType)
    {
        BindingNode node = new BindingNode(tagName, new ListBinding(bindType.createBinding(current.getBinding(), fieldName, null), inputStyle, instanceType));
        node.setReadOnly(readOnly);
        current.addChild(node);
        current = node;
        return this;
    }

    public Binder endList()
    {
        if(!(current.getBinding() instanceof ListBinding)) throw new RuntimeException("endList without bindList");
        current = current.getParent();
        return this;
    }
    
    public BindingNode getBinding()
    {
        return current;
    }
    
    public Binder setAllowConstraintViolations(boolean allowConstraintViolations)
    {
        current.setAllowConstraintViolations(allowConstraintViolations);
        return this;
    }
    
}
