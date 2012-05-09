package org.kopptech.commonbond.resttools;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.kopptech.commonbond.BindingNode;
import org.kopptech.commonbond.ObjUtils;
import org.kopptech.commonbond.json.JsonBindingReader;
import org.kopptech.commonbond.json.JsonBindingSerializer;

/**
 * 
 * See http://java.boot.by/ocewsd6-guide/ch02.html
 *
 * @author nkopp
 */
@Provider
//@Produces("text/json")
@Produces("*/*")
public class JsonTemplateMessageBodyWriter implements MessageBodyWriter {
    @Override
    public long getSize(Object obj, Class type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public boolean isWriteable(Class type, Type genericType,
            Annotation annotations[], MediaType mediaType) {
        if(type.getName().startsWith("com.mostlynumbers.")) return true;
        if(type.getName().startsWith("java.util.")) return true;
        return false;
    }

    @Override
    public void writeTo(Object target, Class type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap httpHeaders, OutputStream outputStream)
            throws IOException {
        
        if(target==null) return;
        
        String templateName = null;
        for(Annotation a : annotations)
        {
            if(a instanceof JsonTemplate)
            {
                templateName = ((JsonTemplate)a).value();
            }
        }
        
        if(templateName==null && target instanceof String)
        {
            outputStream.write(((String)target).getBytes("UTF-8"));
            return;
        }
        
        if(templateName==null)
        {
            templateName = ObjUtils.deriveTemplateName(target, type, genericType, templateName);
        }
        
        try
        {
            if(target instanceof List)
            {
                if(genericType instanceof ParameterizedType)
                {
                    ParameterizedType ptype = (ParameterizedType)genericType;
                    Type[] types = ptype.getActualTypeArguments();
                    if(types.length==0) throw new RuntimeException("type cannot be detected");
                    Class<?> internalType = (Class<?>)types[0];
                    URL url = internalType.getResource(templateName);
                    BindingNode binding = (new JsonBindingReader()).readListBinding(url, (List<?>)target, internalType);
                    String json = (new JsonBindingSerializer()).serialize(binding);
                    outputStream.write(json.getBytes());
                }
                else
                {
                    throw new RuntimeException("type cannot be detected");
                }
            }
            else
            {
                URL url = type.getResource(templateName);
                BindingNode binding = (new JsonBindingReader()).readNonListBinding(url, target);
                String json = (new JsonBindingSerializer()).serialize(binding);
                outputStream.write(json.getBytes());
            }
        }
        catch(Exception e)
        {
            throw ObjUtils.wrap(e);
        }
    }
}