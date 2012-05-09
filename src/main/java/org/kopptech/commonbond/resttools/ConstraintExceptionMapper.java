package org.kopptech.commonbond.resttools;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.kopptech.commonbond.BindingConstraintViolation;
import org.kopptech.commonbond.ConstraintValidationException;

@Provider
public class ConstraintExceptionMapper implements ExceptionMapper<ConstraintValidationException>
{
    public Response toResponse(ConstraintValidationException exception)
    {
        System.err.println(exception.getMessage());
        String json = "[";
        for(BindingConstraintViolation v : exception.getConstraintViolations())
        {
            json += "{\"field\":\""+v.getPath()+"\",\"message\":\""+v.getOriginal().getMessage()+"\"},";
        }
        json = json.substring(0,json.length()-1);
        json += "]";
        return Response.status(Status.BAD_REQUEST).entity(json).type("text/json").build();
    }
}
