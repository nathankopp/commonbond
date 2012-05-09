package org.kopptech.commonbond.bindings;

/**
 * also look at:
 *   JEXL: http://commons.apache.org/jexl/
 *   JXpath: http://commons.apache.org/jxpath/
 *   MVEL: http://mvel.codehaus.org/Home
 *   Spring EL: http://static.springsource.org/spring/docs/3.0.5.RELEASE/reference/expressions.html
 */
import java.lang.reflect.Type;

import org.kopptech.commonbond.Binding;
import org.kopptech.commonbond.ObjUtils;

import ognl.Ognl;
import ognl.OgnlException;


public class OgnlBinding extends AbstractDirectPropertyBinding
{
    private Object expression;
    
    public OgnlBinding(Binding parentBinding, String propName) 
    {
        this(parentBinding, propName, null);
    }

    public OgnlBinding(Binding parentBinding, String propName, Class<?> newInstanceClassOverride)
    {
        super(parentBinding, propName, newInstanceClassOverride);
        try
        {
            expression = Ognl.parseExpression( propName );
        }
        catch(OgnlException e)
        {
            throw ObjUtils.wrap(e);
        }
    }
    
    @Override
    public Type getDetectedTypeFromProperty()
    {
        return Object.class;
    }


    public Object getValue(boolean createParentIfNull)
    {
        try
        {
            return Ognl.getValue( expression, parentBinding.getValue(createParentIfNull) );
        }
        catch (OgnlException e)
        {
            throw ObjUtils.wrap(e);
        }
    }

    @Override
    public void setValueInternal(Object obj)
    {
        try
        {
            Ognl.setValue(expression, parentBinding.getValue(true), obj);
        }
        catch (OgnlException e)
        {
            throw ObjUtils.wrap(e);
        }
    }
}
