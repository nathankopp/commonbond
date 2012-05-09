package org.kopptech.commonbond.json;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.BooleanNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.TextNode;
import org.kopptech.commonbond.BindType;
import org.kopptech.commonbond.Binder;
import org.kopptech.commonbond.BindingNode;
import org.kopptech.commonbond.BindingReader;
import org.kopptech.commonbond.InputStyle;
import org.kopptech.commonbond.ObjUtils;


public class JsonBindingReader implements BindingReader
{
    public BindingNode readListBinding(URL url, List<?> obj, Class<?> newInstanceClass) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonRoot = mapper.readValue(url, JsonNode.class);

        ObjectNode childListBinding = (ObjectNode) jsonRoot.get("list");
        if(childListBinding==null)
        {
            throw new RuntimeException("root object is a List, but binding does not specify list");
        }
        
        InputStyle inputStyle = readInputStyle(childListBinding);
        Class<?> overrideNewInstanceClass = readNewInstanceClass(childListBinding);
        if(overrideNewInstanceClass!=null) newInstanceClass = overrideNewInstanceClass;
        
        Binder binder = Binder.startWithList((List<?>)obj, inputStyle, newInstanceClass);
        readBindingNode(binder, childListBinding);
        
        return binder.getBinding();
    }

    public BindingNode readNonListBinding(URL url, Object obj) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonRoot = mapper.readValue(url, JsonNode.class);

        ObjectNode childListBinding = (ObjectNode) jsonRoot.get("list");
        if(childListBinding!=null)
        {
            throw new RuntimeException("root object a List, but binding specifies NOT a list");
        }
        
        Binder binder = Binder.startWithObj(obj);            
        readBindingNode(binder, (ObjectNode)jsonRoot);
        
        return binder.getBinding();
    }
    
    private void readBindingNode(Binder binder, ObjectNode jsonNode)
    {
        for(Iterator<String> iter = jsonNode.getFieldNames(); iter.hasNext();)
        {
            String tagName = iter.next();
            JsonNode binding = jsonNode.get(tagName);
            if(binding instanceof TextNode)
            {
                String fieldName = binding.getTextValue();
                binder.bindValue(tagName, fieldName, false, BindType.AUTO);
            }
            else if(binding instanceof BooleanNode)
            {
                if(binding.getBooleanValue())
                {
                    String fieldName = tagName;
                    binder.bindValue(tagName, fieldName, false, BindType.AUTO);
                }
            }
            else if (binding instanceof ObjectNode)
            {
                ObjectNode bindingInfo = (ObjectNode) binding;
                String propName = readPropName(bindingInfo);
                BindType bindType = readBindType(bindingInfo);
                ObjectNode childObjBinding = (ObjectNode) bindingInfo.get("obj");
                ObjectNode childListBinding = (ObjectNode) bindingInfo.get("list");
                JsonNode readOnlyNode = (JsonNode) bindingInfo.get("readOnly");
                boolean readOnly = readOnlyNode!=null && readOnlyNode.getBooleanValue();
                if(bindType==BindType.DUMMY)
                {
                    binder.bindObject(tagName, propName, null, readOnly, bindType);
                    readBindingNode(binder, bindingInfo);
                    binder.endObject();
                }
                else if(childObjBinding!=null)
                {
                    //binder.bindObject(tagName, fieldName, ChildObj.class);
                    binder.bindObject(tagName, propName, null, readOnly, bindType);
                    readBindingNode(binder, childObjBinding);
                    binder.endObject();
                }
                else if (childListBinding!=null)
                {
                    InputStyle inputStyle = readInputStyle(bindingInfo);
                    Class<?> newInstanceClass = readNewInstanceClass(bindingInfo);
                    binder.bindList(tagName, propName, inputStyle, newInstanceClass, readOnly, bindType);
                    readBindingNode(binder, childListBinding);
                    binder.endList();
                }
                else
                {
                    binder.bindValue(tagName, propName, readOnly, bindType);
                }
            }
        }
    }

    private Class<?> readNewInstanceClass(ObjectNode bindingInfo)
    {
        JsonNode newInstanceClassNode = bindingInfo.get("newInstanceClass");
        Class<?> newInstanceClass = null;
        try
        {
            newInstanceClass = newInstanceClassNode==null?null:Class.forName(newInstanceClassNode.getTextValue());
        }
        catch (ClassNotFoundException e)
        {
            throw ObjUtils.wrap(e);
        }
        return newInstanceClass;
    }

    private InputStyle readInputStyle(ObjectNode bindingInfo)
    {
        JsonNode input = bindingInfo.get("input");
        InputStyle inputStyle = input==null?InputStyle.HYBRID_AUTO:InputStyle.valueOf(input.getTextValue().toUpperCase());
        return inputStyle;
    }
    private String readPropName(ObjectNode bindingInfo)
    {
        JsonNode input = bindingInfo.get("bind");
        if(input!=null) return input.getTextValue();
        input = bindingInfo.get("var");
        if(input!=null) return input.getTextValue();
        input = bindingInfo.get("prop");
        if(input!=null) return input.getTextValue();
        input = bindingInfo.get("ognl");
        if(input!=null) return input.getTextValue();
        input = bindingInfo.get("el");
        if(input!=null) return input.getTextValue();
        return null;
    }
    
    private BindType readBindType(ObjectNode bindingInfo)
    {
        JsonNode input = bindingInfo.get("bind");
        if(input!=null) return BindType.AUTO;
        input = bindingInfo.get("var");
        if(input!=null) return BindType.FIELD;
        input = bindingInfo.get("prop");
        if(input!=null) return BindType.METHOD;
        input = bindingInfo.get("ognl");
        if(input!=null) return BindType.OGNL;
        input = bindingInfo.get("el");
        if(input!=null) return BindType.EL;
        return BindType.DUMMY;
    }
    
    
}
