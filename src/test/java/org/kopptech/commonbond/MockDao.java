package org.kopptech.commonbond;

import java.util.HashMap;
import java.util.Map;

import org.kopptech.commonbond.DaoForBinding;

public class MockDao implements DaoForBinding
{
    Map<Class<?>,Map<String, Object>> data = new HashMap<Class<?>,Map<String, Object>>();
    
    public MockDao()
    {
        Map<String, Object> childWithId = new HashMap<String, Object>();
        childWithId.put("1", new ChildWithId("1","1-data"));
        childWithId.put("2", new ChildWithId("2","2-data"));
        childWithId.put("3", new ChildWithId("3","3-data"));
        data.put(ChildWithId.class, childWithId);
    }

    public Object loadById(Class<?> type, String id)
    {
        if(data.get(type)==null) return null;
        return data.get(type).get(id);
    }

}
