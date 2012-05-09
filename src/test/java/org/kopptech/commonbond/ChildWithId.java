package org.kopptech.commonbond;

public class ChildWithId
{
    private String id;
    private String data;
 
    public ChildWithId()
    {
    }
    
    public ChildWithId(String id, String data)
    {
        super();
        this.id = id;
        this.data = data;
    }
    
    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }
    public String getData()
    {
        return data;
    }
    public void setData(String data)
    {
        this.data = data;
    }
}
