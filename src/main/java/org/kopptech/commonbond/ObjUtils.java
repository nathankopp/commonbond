package org.kopptech.commonbond;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.kopptech.util.Util;

public class ObjUtils
{

    public static Field findDeclaredField(Class<? extends Object> class1, String fieldName)
    {
        while(!class1.getName().equals("java.lang.Object"))
        {
            for(Field f : class1.getDeclaredFields())
            {
                if(f.getName().equals(fieldName))
                {
                    f.setAccessible(true);
                    return f;
                }
            }
            class1 = class1.getSuperclass();
        }
        return null;
    }

    public static RuntimeException wrap(Exception e)
    {
        if (e instanceof RuntimeException) return (RuntimeException) e;
        else return new RuntimeException(e);
    }

    public static boolean equal(Object o1, Object o2)
    {
        if(o1==o2) return true;
        if(o1==null || o2==null) return false;
        return o1.equals(o2);
    }

    public static Method findGetterMethod(Class<? extends Object> class1, String propName)
    {
        String methodName1 = "get"+capatalizeFirst(propName);
        String methodName2 = "is"+capatalizeFirst(propName);
        for(Method m : class1.getMethods())
        {
            if(m.getName().equals(methodName1) || m.getName().equals(methodName2))
            {
                m.setAccessible(true);
                return m;
            }
        }
        return null;
    }

    public static Method findSetterMethod(Class<? extends Object> class1, String propName)
    {
        String methodName = "set"+capatalizeFirst(propName);
        for(Method m : class1.getMethods())
        {
            if(m.getName().equals(methodName))
            {
                m.setAccessible(true);
                return m;
            }
        }
        return null;
    }

    private static String capatalizeFirst(String propName)
    {
        if (Util.isBlank(propName)) return propName;

        return Character.toUpperCase(propName.charAt(0)) + propName.substring(1);
    }

    public static String findId(Object o)
    {
        try
        {
            Field f = findDeclaredField(o.getClass(), "id");
            if(f!=null) return f.get(o).toString();
            Method m = findGetterMethod(o.getClass(), "id");
            if(m!=null) return m.invoke(o).toString();
            return null;
        }
        catch(Exception e)
        {
            throw wrap(e);
        }
    }
    
    public static void setId(Object o, String id)
    {
        try
        {
            Field f = findDeclaredField(o.getClass(), "id");
            if(f!=null)
            {
                f.set(o, id);
                return;
            }
            Method m = findSetterMethod(o.getClass(), "id");
            if(m!=null)
            {
                m.invoke(o, id);
            }
        }
        catch(Exception e)
        {
            throw wrap(e);
        }
    }

    public static String convertSingleQuoteToDouble(String string)
    {
        return string.replace("'", "\"");
    }

    public static Object convertIfNecessary(Object obj, Class<?> targetType) throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException
    {
        if(obj!=null && !targetType.isAssignableFrom(obj.getClass()))
        {
            obj = targetType.getMethod("valueOf", String.class).invoke(null, obj.toString());
        }
        return obj;
    }

    public static String deriveTemplateName(Object target, Class<?> type, Type genericType, String templateName)
    {
        if(target instanceof List)
        {
            if(genericType instanceof ParameterizedType)
            {
                ParameterizedType ptype = (ParameterizedType)genericType;
                Type[] types = ptype.getActualTypeArguments();
                if(types.length==0) throw new RuntimeException("type cannot be detected");
                templateName = "ListOf"+((Class<?>)types[0]).getSimpleName()+".txt";
            }
            else
            {
                throw new RuntimeException("type cannot be detected");
            }
        }
        else
        {
            templateName = type.getSimpleName()+".txt";
        }
        return templateName;
    }


}
