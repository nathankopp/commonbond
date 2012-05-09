package org.kopptech.commonbond;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.kopptech.commonbond.bindings.ListBinding;
import org.kopptech.commonbond.bindings.PropertyBinding;


public class BindingNode
{
    protected BindingNode parent;
    protected List<BindingNode> children;
    protected String tagName;
    protected Binding binding;
    protected boolean readOnly;
    
    public BindingNode(Binding binding)
    {
        super();
        this.binding = binding;
    }
    

    public BindingNode(String tagName, Binding binding)
    {
        super();
        this.tagName = tagName;
        this.binding = binding;
    }
    
    public List<BindingConstraintViolation> getConstraintViolations()
    {
        List<BindingConstraintViolation> retVal = new ArrayList<BindingConstraintViolation>();
        if(binding instanceof PropertyBinding)
        {
            Set<ConstraintViolation<?>> violations = ((PropertyBinding) binding).getConstraintViolations();
            if(violations!=null)
            {
                for(ConstraintViolation<?> cv : violations)
                {
                    BindingConstraintViolation bcv = new BindingConstraintViolation(cv, getPath());
                    retVal.add(bcv);
                }
            }
        }
        if(children!=null)
        {
            for(BindingNode child : children)
            {
                retVal.addAll(child.getConstraintViolations());
            }
        }
        return retVal;
    }


    private String getPath()
    {
        String path;
        if(parent!=null && !parent.getPath().isEmpty()) path = parent.getPath()+"."+tagName;
        else if (tagName!=null) path = tagName;
        else path = "";
        if(binding instanceof ListBinding)
        {
            path += "["+((ListBinding)binding).getCurrentIdx()+"]";
        }
        return path;
    }


    public Object getValue()
    {
        return binding.getValue(true);
    }
    
    public void setValue(Object obj, boolean allowInvalid)
    {
        if(isReadOnly()) return;
        binding.setValue(obj, allowInvalid);
    }
    
    public boolean isAllowConstraintViolations()
    {
        return binding.isAllowConstraintViolations();
    }

    public void setAllowConstraintViolations(boolean allowConstraintViolations)
    {
        binding.setAllowConstraintViolations(allowConstraintViolations);
    }
    
    
    public void addChild(BindingNode binding)
    {
        if(!(binding.getBinding() instanceof PropertyBinding))
            throw new RuntimeException("child bindings must be of type PropertyBinding (for "+binding.getTagName()+")");
            
        if(children==null) children = new ArrayList<BindingNode>();
        binding.setParent(this);
        children.add(binding);
    }

    public BindingNode getChildByFieldName(String fieldName)
    {
        if(children==null) return null;
        for(BindingNode child : children)
        {
            if(ObjUtils.equal(((PropertyBinding)child.getBinding()).getPropName(),fieldName)) return child;
        }
        return null;
    }
    
    public BindingNode getChildByTagName(String tagName)
    {
        if(children==null) return null;
        for(BindingNode child : children)
        {
            if(ObjUtils.equal(child.getTagName(),tagName)) return child;
        }
        return null;
    }

    public boolean hasChildren()
    {
        return getChildren()!=null && !getChildren().isEmpty();
    }

    
    public BindingNode getParent()
    {
        return parent;
    }
    public void setParent(BindingNode parent)
    {
        this.parent = parent;
    }
    public List<BindingNode> getChildren()
    {
        return children;
    }
    public void setChildren(List<BindingNode> children)
    {
        this.children = children;
    }
    public String getTagName()
    {
        return tagName;
    }
    public void setTagName(String tagName)
    {
        this.tagName = tagName;
    }
    public Binding getBinding()
    {
        return binding;
    }
    public void setBinding(Binding binding)
    {
        this.binding = binding;
    }


    public boolean isReadOnly()
    {
        return readOnly;
    }


    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }


    
}
