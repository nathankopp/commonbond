package org.kopptech.commonbond;

import javax.validation.constraints.NotNull;

public class ChildObjWithConstraints
{
    @NotNull
    private Object x;
    @NotNull
    private Object y;
    
    public ChildObjWithConstraints(Object x, Object y)
    {
        super();
        this.x = x;
        this.y = y;
    }
    public ChildObjWithConstraints()
    {
        
    }
    
    public Object getX()
    {
        return x;
    }
    public void setX(Object x)
    {
        this.x = x;
    }
    public Object getY()
    {
        return y;
    }
    public void setY(Object y)
    {
        this.y = y;
    }
}
