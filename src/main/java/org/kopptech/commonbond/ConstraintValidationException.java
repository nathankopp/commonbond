package org.kopptech.commonbond;

import java.util.List;

public class ConstraintValidationException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    List<BindingConstraintViolation> constraintViolations;

    public ConstraintValidationException()
    {
        super();
    }

    public ConstraintValidationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConstraintValidationException(String message)
    {
        super(message);
    }

    public ConstraintValidationException(Throwable cause)
    {
        super(cause);
    }

    public ConstraintValidationException(List<BindingConstraintViolation> constraintViolations)
    {
        this.constraintViolations = constraintViolations;
    }
    
    @Override
    public String getMessage()
    {
        if(constraintViolations!=null)
        {
            String message = "";
            for(BindingConstraintViolation v : constraintViolations)
            {
                message += v.getPath()+" "+v.getOriginal().getMessage()+"\n";
            }
            return message;
        }
        else
        {
            return super.getMessage();
        }
    }

    public List<BindingConstraintViolation> getConstraintViolations()
    {
        return constraintViolations;
    }
    
    

}
