package org.kopptech.commonbond.resttools;

import java.util.List;

import org.kopptech.commonbond.BindingConstraintViolation;
import org.kopptech.commonbond.BindingNode;
import org.kopptech.commonbond.ObjUtils;
import org.kopptech.commonbond.json.JsonBindingReader;
import org.kopptech.commonbond.json.JsonBindingSerializer;

public class ObjectReader
{
    private List<BindingConstraintViolation> constraintViolations;
    
    public <T> T parseNewObject(String json, Class<T> type, boolean allowInvalidValues)
    {
        return parseNewObject(json, type, allowInvalidValues, null);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T parseNewObject(String json, Class<T> type, boolean allowInvalidValues, String templateName)
    {
        try
        {
            Object target = type.newInstance();
            parseExistingObject(target, json, type, allowInvalidValues, templateName);
            return (T)target;
        }
        catch(Exception e)
        {
            throw ObjUtils.wrap(e);
        }
    }

    public void parseExistingObject(String json, Class<?> type, boolean allowInvalidValues, Object target)
    {
        parseExistingObject(target, json, type, allowInvalidValues, null);
    }
    
    public void parseExistingObject(Object target, String json, Class<?> type, boolean allowInvalidValues, String templateName)
    {
        if(json==null) return;
        try
        {
            if(templateName==null) templateName = type.getSimpleName()+".txt";
            BindingNode binding = (new JsonBindingReader()).readNonListBinding(type.getResource(templateName), target);
            
            JsonBindingSerializer ser = new JsonBindingSerializer();
            ser.setAllowInvalidValues(allowInvalidValues);
            ser.deserialize(binding, json);
            
            List<BindingConstraintViolation> newViolations = binding.getConstraintViolations();
            if(constraintViolations==null)
                constraintViolations = newViolations;
            else
                constraintViolations.addAll(newViolations);
        }
        catch(Exception e)
        {
            throw ObjUtils.wrap(e);
        }
    }

    public List<BindingConstraintViolation> getConstraintViolations()
    {
        return constraintViolations;
    }
}
