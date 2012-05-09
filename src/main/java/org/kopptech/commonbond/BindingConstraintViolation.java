package org.kopptech.commonbond;

import javax.validation.ConstraintViolation;

public class BindingConstraintViolation
{
    private ConstraintViolation<?> original;
    private String path;
    
    public BindingConstraintViolation(ConstraintViolation<?> original, String path)
    {
        super();
        this.original = original;
        this.path = path;
    }
    
    public ConstraintViolation<?> getOriginal()
    {
        return original;
    }
    public void setOriginal(ConstraintViolation<?> original)
    {
        this.original = original;
    }
    public String getPath()
    {
        return path;
    }
    public void setPath(String path)
    {
        this.path = path;
    }
}
