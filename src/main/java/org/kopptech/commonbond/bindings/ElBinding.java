package org.kopptech.commonbond.bindings;

import java.lang.reflect.Type;
import java.util.Set;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.validation.Validation;
import javax.validation.Validator;

import org.kopptech.commonbond.Binding;


import de.odysseus.el.util.SimpleContext;

public class ElBinding extends AbstractDirectPropertyBinding
{
    private ValueExpression expression;
    private ValueExpression beanExpression;
    
    public ElBinding(Binding parentBinding, String propName) 
    {
        this(parentBinding, propName, null);
    }

    public ElBinding(Binding parentBinding, String propName, Class<?> newInstanceClassOverride)
    {
        super(parentBinding, propName, newInstanceClassOverride);
        ExpressionFactory factory = ExpressionFactory.newInstance();
        SimpleContext context = createContext(parentBinding);
        expression = factory.createValueExpression(context,"#{root."+propName+"}", Object.class);
        if(propName.lastIndexOf(".")>0)
        {
            beanExpression = factory.createValueExpression(context,"#{root."+propName.substring(0, propName.lastIndexOf("."))+"}", Object.class);
        }
    }

    @SuppressWarnings({ "rawtypes" })
    @Override
    protected Set validateProperty(Object o)
    {
        if(beanExpression==null)
        {
            return super.validateProperty(o);
        }
        else
        {
            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
            SimpleContext context = createContext(parentBinding);
            Class<?> beanType = determineBeanType(context);
            if(beanType==null) return super.validateProperty(o);
            else return (Set)validator.validateValue(beanType, propName.substring(propName.lastIndexOf(".")+1), o);
        }
    }

    @Override
    public Type getDetectedTypeFromProperty()
    {
        SimpleContext context = createContext(parentBinding);
        return expression.getType(context);
    }


    public Object getValue(boolean createParentIfNull)
    {
        SimpleContext context = createContext(parentBinding);
        return expression.getValue(context);
    }

    @Override
    public void setValueInternal(Object obj)
    {
        SimpleContext context = createContext(parentBinding);
        expression.setValue(context, obj);
    }

    private SimpleContext createContext(Binding parentBinding)
    {
        ExpressionFactory factory = ExpressionFactory.newInstance();
        SimpleContext context = new de.odysseus.el.util.SimpleContext();
        Object parentVal = parentBinding.getValue(false);
        if(parentVal==null) parentVal = parentBinding.createDefaultValue();
        context.setVariable("root", factory.createValueExpression(parentVal, determineParentBindingType(parentBinding)));
        return context;
    }

    private Class<?> determineParentBindingType(Binding parentBinding)
    {
        Class<?> parentBindingType = null;
        if(parentBinding.getValue(false)!=null) parentBindingType = parentBinding.getValue(false).getClass();
        else parentBindingType = (Class<?>)parentBinding.getType();
        if(parentBindingType==null) parentBindingType = parentBinding.createDefaultValue().getClass();
        return parentBindingType;
    }
    
    private Class<?> determineBeanType(SimpleContext context)
    {
        Object obj = beanExpression.getValue(context);
        if(obj!=null) return obj.getClass();
        else return beanExpression.getType(context);
    }
    

}
