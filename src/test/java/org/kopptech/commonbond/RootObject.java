package org.kopptech.commonbond;

import java.util.ArrayList;
import java.util.List;

public class RootObject
{
    private String val1;
    private String val2;
    private ChildObj obj1;
    private List<ChildObj> list1;
    
    public RootObject()
    {
    }
    
    public RootObject(String val1, String val2, ChildObj obj1, List<ChildObj> list1)
    {
        super();
        this.val1 = val1;
        this.val2 = val2;
        this.obj1 = obj1;
        this.list1 = list1;
    }
    
    public void addChild(ChildObj obj)
    {
        if(list1==null) list1 = new ArrayList<ChildObj>();
        list1.add(obj);
    }
    
    
    public String getVal1()
    {
        return val1;
    }
    public void setVal1(String val1)
    {
        this.val1 = val1;
    }
    public String getVal2()
    {
        return val2;
    }
    public void setVal2(String val2)
    {
        this.val2 = val2;
    }
    public ChildObj getObj1()
    {
        return obj1;
    }
    public void setObj1(ChildObj obj1)
    {
        this.obj1 = obj1;
    }
    public List<ChildObj> getList1()
    {
        return list1;
    }
    public void setList1(List<ChildObj> list1)
    {
        this.list1 = list1;
    }
}
