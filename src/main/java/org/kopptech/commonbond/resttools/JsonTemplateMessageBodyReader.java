package org.kopptech.commonbond.resttools;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.kopptech.commonbond.BindingNode;
import org.kopptech.commonbond.JsonTemplate;
import org.kopptech.commonbond.ObjUtils;
import org.kopptech.commonbond.json.JsonBindingReader;
import org.kopptech.commonbond.json.JsonBindingSerializer;

@Provider
@Consumes("application/json")
public class JsonTemplateMessageBodyReader implements MessageBodyReader
{

    @Override
    public boolean isReadable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        if(type.getName().startsWith("com.mostlynumbers")) return true;
        return false;
    }

    @Override
    public Object readFrom(Class type, Type genericType, Annotation[] annotations, MediaType mediaType,
                      MultivaluedMap httpHeaders, InputStream entityStream) throws IOException,
            WebApplicationException
    {
        String templateName = null;
        for(Annotation a : annotations)
        {
            if(a instanceof JsonTemplate)
            {
                templateName = ((JsonTemplate)a).value();
            }
        }
        
        if(templateName==null)
        {
            templateName = type.getSimpleName()+".txt";
        }
        
        try
        {
            Object target = type.newInstance();
            BindingNode binding = (new JsonBindingReader()).readNonListBinding(type.getResource(templateName), target);
            
            StringWriter writer = new StringWriter();
            IOUtils.copy(entityStream, writer, "UTF-8");
            String json = writer.toString();
            
            (new JsonBindingSerializer()).deserialize(binding, json);
            
            return target;
        }
        catch(Exception e)
        {
            throw ObjUtils.wrap(e);
        }
    }

}
