package org.kopptech.commonbond.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.kopptech.commonbond.BindingNode;
import org.kopptech.commonbond.BindingSerializer;
import org.kopptech.commonbond.DaoForBinding;
import org.kopptech.commonbond.InputStyle;
import org.kopptech.commonbond.bindings.ListBinding;


public class JsonBindingSerializer implements BindingSerializer
{
    boolean allowInvalidValues = false;
    
    public String serialize(BindingNode root) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = createJsonNode(mapper, root);
        return mapper.writeValueAsString(node);
    }
    public void deserialize(BindingNode rootNode, String json) throws Exception
    {
        deserialize(rootNode, json, null);
    }
    
    public void deserialize(BindingNode rootNode, String json, DaoForBinding daoForBinding) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonRoot = mapper.readValue(json, JsonNode.class);
        
        readJsonNode(rootNode, jsonRoot, daoForBinding);
    }

    

    public JsonNode nodeForValue(Object v)
    {
        if(v == null) return JsonNodeFactory.instance.nullNode();
        else if(v instanceof byte[]) return JsonNodeFactory.instance.binaryNode((byte[])v);
        else if(v instanceof Boolean) return JsonNodeFactory.instance.booleanNode((Boolean)v);
        else if(v instanceof BigDecimal) return JsonNodeFactory.instance.numberNode((BigDecimal)v);
        else if(v instanceof BigInteger) return JsonNodeFactory.instance.numberNode((BigInteger)v);
        else if(v instanceof Byte) return JsonNodeFactory.instance.numberNode((Byte)v);
        else if(v instanceof Double) return JsonNodeFactory.instance.numberNode((Double)v);
        else if(v instanceof Float) return JsonNodeFactory.instance.numberNode((Float)v);
        else if(v instanceof Integer) return JsonNodeFactory.instance.numberNode((Integer)v);
        else if(v instanceof Long) return JsonNodeFactory.instance.numberNode((Long)v);
        else if(v instanceof Short) return JsonNodeFactory.instance.numberNode((Short)v);
        else return JsonNodeFactory.instance.textNode(v.toString());
    }

    public Object valueForNode(JsonNode node, Type v) throws IOException
    {
        if(node==null) return null;
        if(node.isNull()) return null;
        if(node.isMissingNode()) return null;
        if(node.isNumber()) return node.getNumberValue();
        if(node.isBinary()) return node.getBinaryValue();
        if(node.isTextual()) return node.getTextValue();
        throw new RuntimeException("node type not supported");
    }

    public ObjectNode createObjNode(ObjectMapper mapper, BindingNode binding)
    {
        ObjectNode node = mapper.createObjectNode();
        for(BindingNode child : binding.getChildren())
        {
            JsonNode newNode = createJsonNode(mapper, child);
            node.put(child.getTagName(), newNode);
        }
        return node;
    }

    private JsonNode createJsonNode(ObjectMapper mapper, BindingNode bindingNode)
    {
        JsonNode newNode;
        if (bindingNode.getBinding() instanceof ListBinding)
        {
            newNode = createListNode(mapper, bindingNode);
        }
        else
        {
            if(bindingNode.hasChildren())
            {
                newNode = createObjNode(mapper, bindingNode);
            }
            else
            {
                newNode = nodeForValue(bindingNode.getValue());
            }
        }
        return newNode;
    }

    public JsonNode createListNode(ObjectMapper mapper, BindingNode binding)
    {
        ArrayNode node = mapper.createArrayNode();
        
        for(int i = 0; i<((ListBinding)binding.getBinding()).getAssociatedListObject().size(); i++)
        {
            ((ListBinding)binding.getBinding()).setCurrentIdx(i);
            node.add(createObjNode(mapper, binding));
        }
        
        return node;
    }

    private void readObjectNode(BindingNode bindingNode, JsonNode jsonNode, DaoForBinding daoForBinding) throws IOException
    {
        if(bindingNode.isReadOnly()) return;
        if(jsonNode==null)
        {
            bindingNode.setValue(null, allowInvalidValues);
            return;
        }
        ObjectNode objNode = (ObjectNode)jsonNode;
        for(BindingNode child : bindingNode.getChildren())
        {
            JsonNode childJsonNode = objNode.get(child.getTagName());
            readJsonNode(child, childJsonNode, daoForBinding);
        }
    }

    private void readJsonNode(BindingNode bindingNode, JsonNode jsonNode, DaoForBinding daoForBinding) throws IOException
    {
        if(bindingNode.isReadOnly()) return;
        if (bindingNode.getBinding() instanceof ListBinding)
        {
            ArrayNode listNode = (ArrayNode)jsonNode;
            readListNode(listNode, bindingNode, daoForBinding);
        }
        else
        {
            if(bindingNode.hasChildren())
            {
                readObjectNode(bindingNode, jsonNode, daoForBinding);
            }
            else
            {
                if(bindingNode.getBinding().getType()==null)
                    throw new RuntimeException("type cannot be null");
                
                // note... remove this "if" (and always call setValue) if we want missing fields to be interpreted as null values
                if(jsonNode!=null)
                    bindingNode.setValue(valueForNode(jsonNode, bindingNode.getBinding().getType()), allowInvalidValues);
            }
        }
    }

    private void readListNode(ArrayNode listNode, BindingNode bindingNode, DaoForBinding daoForBinding) throws IOException
    {
        if(bindingNode.isReadOnly()) return;
        ListBinding listBinding = (ListBinding)bindingNode.getBinding();
        if(listNode==null)
        {
            listBinding.getRootBinding().setValue(null, allowInvalidValues);
            return;
        }
        InputStyle style = listBinding.getInputStyle();
        if(style==InputStyle.HYBRID_AUTO)
        {
            style = autoDetectInputStyle(listNode);
        }
        
        switch(style)
        {
            case SELECT_BY_ID:
                List<Object> newList = new ArrayList<Object>();
                for(int i = 0; i<listNode.size(); i++)
                {
                    JsonNode idNode = listNode.get(i).get("id");
                    String id = idNode==null?null:idNode.getTextValue();
                    if(id==null) throw new RuntimeException("cannot find id for object in list "+bindingNode.getTagName());
                    int idx = listBinding.findIdxById(id);
                    Object existing = idx<0?null:listBinding.getAssociatedListObject().get(idx);
                    if(existing==null)
                    {
                        if(daoForBinding==null) throw new RuntimeException("daoForBinding not specified");
                        newList.add(daoForBinding.loadById((Class<?>)listBinding.getType(), id));
                    }
                    else
                    {
                        newList.add(existing);
                    }
                }
                listBinding.getAssociatedListObject().clear();
                for(Object o : newList)
                {
                    listBinding.addNewValueAndSetAsCurrent(o);
                }
                break;
                
            case EDIT_BY_ORDER:
                if (listBinding.getAssociatedListObject().size()!=listNode.size()) throw new RuntimeException("size must match in the list "+listBinding.getPropName());
            case HYBRID_BY_ORDER:
                for(int i = 0; i<listNode.size(); i++)
                {
                    listBinding.setCurrentIdx(i);
                    readObjectNode(bindingNode, listNode.get(i), daoForBinding);
                }
                listBinding.deleteRemaining();
                break;
                
            case EDIT_BY_ID:
                if(listBinding.getAssociatedListObject().size()<listNode.size()) throw new RuntimeException("too many inputs in the list "+listBinding.getPropName());
            case HYBRID_BY_ID:
                for(int i = 0; i<listNode.size(); i++)
                {
                    JsonNode idNode = listNode.get(i).get("id");
                    String id = idNode==null?null:idNode.getTextValue();
                    int idx = id==null?-1:listBinding.findIdxById(id);
                    if(idx<0)
                    {
                        if(listBinding.getInputStyle()==InputStyle.EDIT_BY_ID) throw new RuntimeException("could not find existing object with id of "+id+" in the list "+listBinding.getPropName());
                        
                        if(daoForBinding!=null)
                        {
                            listBinding.addNewValueAndSetAsCurrent(daoForBinding.loadById((Class<?>)listBinding.getType(), id));
                        }
                        else
                        {
                            listBinding.addNewValueAndSetAsCurrent(listBinding.createDefaultValue());
                        }
                    }
                    else
                    {
                        listBinding.setCurrentIdx(idx);
                    }
                    readObjectNode(bindingNode, listNode.get(i), daoForBinding);
                }
                break;
                
        }
        
        
    }

    private InputStyle autoDetectInputStyle(ArrayNode listNode)
    {
        InputStyle style;
        boolean oneHasId = false;
        for(int i = 0; i<listNode.size(); i++)
        {
            if(listNode.get(i).get("id")!=null)
            {
                oneHasId = true;
                break;
            }
        }
        if(oneHasId)
            style = InputStyle.HYBRID_BY_ID;
        else
            style = InputStyle.HYBRID_BY_ORDER;
        return style;
    }
    public boolean isAllowInvalidValues()
    {
        return allowInvalidValues;
    }
    public void setAllowInvalidValues(boolean allowInvalidValues)
    {
        this.allowInvalidValues = allowInvalidValues;
    }


}
