package org.kopptech.commonbond;

public class ChildObj
{
    private Object x;
    private Object y;
    
    public ChildObj(Object x, Object y)
    {
        super();
        this.x = x;
        this.y = y;
    }
    public ChildObj()
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
