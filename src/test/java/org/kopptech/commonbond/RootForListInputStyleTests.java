package org.kopptech.commonbond;

import java.util.ArrayList;
import java.util.List;

public class RootForListInputStyleTests
{
    private List<ChildWithId> listWithIds;
    private List<ChildObj> listWithoutIds;
    
    public RootForListInputStyleTests()
    {
    }
    
    public RootForListInputStyleTests addChild(ChildWithId withId)
    {
        if(listWithIds==null) listWithIds = new ArrayList<ChildWithId>();
        listWithIds.add(withId);
        return this;
    }
    public RootForListInputStyleTests addChild(ChildObj withId)
    {
        if(listWithoutIds==null) listWithoutIds = new ArrayList<ChildObj>();
        listWithoutIds.add(withId);
        return this;
    }

    public List<ChildWithId> getListWithIds()
    {
        return listWithIds;
    }
    public void setListWithIds(List<ChildWithId> listWithIds)
    {
        this.listWithIds = listWithIds;
    }
    public List<ChildObj> getListWithoutIds()
    {
        return listWithoutIds;
    }
    public void setListWithoutIds(List<ChildObj> listWithoutIds)
    {
        this.listWithoutIds = listWithoutIds;
    }
}
